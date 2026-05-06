package com.yourcompany.sales.modules.order.repository;

import com.yourcompany.sales.modules.order.entity.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<SalesOrderItem, Long> {

    List<SalesOrderItem> findByOrderId(Long orderId);

    void deleteByOrderId(Long orderId);

    @Query("""
            select i
            from SalesOrderItem i
            join fetch i.order o
            where o.deletedFlag = 0
              and o.createdAt between :start and :end
              and (:allData = true or o.ownerUserId in :ownerUserIds)
            """)
    List<SalesOrderItem> findDashboardItems(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end,
                                            @Param("allData") boolean allData,
                                            @Param("ownerUserIds") List<Long> ownerUserIds);
}