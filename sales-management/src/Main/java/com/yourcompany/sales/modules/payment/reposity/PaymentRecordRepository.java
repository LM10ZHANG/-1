package com.yourcompany.sales.modules.payment.reposity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yourcompany.sales.modules.payment.entity.PaymentRecord;

public interface PaymentRecordRepository
        extends JpaRepository<PaymentRecord, Long> {

    List<PaymentRecord> findByOrderId(Long orderId);

    @Query("SELECT COALESCE(SUM(p.payAmount),0) FROM PaymentRecord p " +
           "WHERE p.orderId = :orderId AND p.status = 'VALID'")
    BigDecimal sumValidAmountByOrderId(Long orderId);

    @Query("""
           SELECT COALESCE(SUM(p.payAmount), 0)
           FROM PaymentRecord p
           WHERE p.payTime BETWEEN :start AND :end
             AND p.status = 'VALID'
           """)
    BigDecimal sumByTimeRange(@Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);

    List<PaymentRecord> findByPayTimeBetweenAndStatus(LocalDateTime start, LocalDateTime end, String status);
}
