package com.yourcompany.sales.modules.payment.reposity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yourcompany.sales.modules.payment.entity.PaymentRecord;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    List<PaymentRecord> findByOrderId(Long orderId);

    @Query("""
            select coalesce(sum(p.payAmount), 0)
            from PaymentRecord p
            where p.orderId = :orderId
              and p.status = 'VALID'
            """)
    BigDecimal sumValidAmountByOrderId(@Param("orderId") Long orderId);

    @Query("""
            select coalesce(sum(p.payAmount), 0)
            from PaymentRecord p
            where p.payTime between :start and :end
              and p.status = 'VALID'
            """)
    BigDecimal sumByTimeRange(@Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);

    @Query("""
            select coalesce(sum(p.payAmount), 0)
            from PaymentRecord p
            where p.status = 'VALID'
              and p.payTime between :start and :end
            """)
    BigDecimal sumValidPaymentAmount(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

    List<PaymentRecord> findByPayTimeBetweenAndStatus(LocalDateTime start,
                                                      LocalDateTime end,
                                                      String status);
}
