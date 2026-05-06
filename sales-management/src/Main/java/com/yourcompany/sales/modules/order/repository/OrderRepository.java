package com.yourcompany.sales.modules.order.repository;

import com.yourcompany.sales.modules.order.entity.SalesOrder;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<SalesOrder, Long>,
        JpaSpecificationExecutor<SalesOrder> {

    Optional<SalesOrder> findByOrderNo(String orderNo);

    boolean existsByOrderNo(String orderNo);

    List<SalesOrder> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @EntityGraph(attributePaths = "items")
    @Query("""
            select o
            from SalesOrder o
            where o.deletedFlag = 0
              and o.createdAt between :start and :end
            """)
    List<SalesOrder> findAllWithItemsByCreatedAtBetween(@Param("start") LocalDateTime start,
                                                        @Param("end") LocalDateTime end);

    @Query("""
            select count(o)
            from SalesOrder o
            where o.deletedFlag = 0
              and o.orderStatus in (
                  com.yourcompany.sales.common.enums.OrderStatus.WAIT_STOCK,
                  com.yourcompany.sales.common.enums.OrderStatus.WAIT_OUTBOUND
              )
            """)
    Long countPendingOutboundOrders();

    @Query("""
            select count(o)
            from SalesOrder o
            where o.deletedFlag = 0
              and o.paymentStatus <> com.yourcompany.sales.common.enums.PaymentStatus.PAID
            """)
    Long countPendingReceivableOrders();

    @Query("""
            select coalesce(sum(o.totalAmount), 0)
            from SalesOrder o
            where o.deletedFlag = 0
              and o.createdAt between :start and :end
            """)
    BigDecimal sumOrderAmount(@Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);

    @Query("""
            select count(o)
            from SalesOrder o
            where o.deletedFlag = 0
              and o.createdAt between :start and :end
            """)
    Long countOrders(@Param("start") LocalDateTime start,
                     @Param("end") LocalDateTime end);

    @Query("""
            select count(o)
            from SalesOrder o
            where o.deletedFlag = 0
              and o.orderStatus = com.yourcompany.sales.common.enums.OrderStatus.COMPLETED
              and o.createdAt between :start and :end
            """)
    Long countCompletedOrders(@Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);

    @Query("""
            select o
            from SalesOrder o
            where o.deletedFlag = 0
              and o.paymentStatus <> com.yourcompany.sales.common.enums.PaymentStatus.PAID
              and o.createdAt < :deadline
            order by o.createdAt asc
            """)
    List<SalesOrder> findOverdueReceivableOrders(@Param("deadline") LocalDateTime deadline);
}
