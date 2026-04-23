package com.yourcompany.sales.modules.payment.reposity;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yourcompany.sales.modules.payment.entity.InvoiceRecord;

public interface InvoiceRecordRepository
        extends JpaRepository<InvoiceRecord, Long> {

    List<InvoiceRecord> findByOrderId(Long orderId);

    @Query("SELECT COALESCE(SUM(i.invoiceAmount), 0) FROM InvoiceRecord i " +
           "WHERE i.orderId = :orderId AND i.invoiceStatus = 'ISSUED'")
    BigDecimal sumIssuedAmountByOrderId(Long orderId);
}
