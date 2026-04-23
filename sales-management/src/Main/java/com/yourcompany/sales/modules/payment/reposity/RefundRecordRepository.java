package com.yourcompany.sales.modules.payment.reposity;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yourcompany.sales.modules.payment.entity.RefundRecord;

public interface RefundRecordRepository
        extends JpaRepository<RefundRecord, Long> {

    List<RefundRecord> findByOrderId(Long orderId);

    @Query("SELECT COALESCE(SUM(r.refundAmount),0) FROM RefundRecord r " +
           "WHERE r.orderId = :orderId AND r.status = 'FINISHED'")
    BigDecimal sumFinishedAmountByOrderId(Long orderId);

    @Query("SELECT COALESCE(SUM(r.refundAmount),0) FROM RefundRecord r " +
           "WHERE r.paymentId = :paymentId AND r.status = 'FINISHED'")
    BigDecimal sumFinishedByPaymentId(Long paymentId);
}