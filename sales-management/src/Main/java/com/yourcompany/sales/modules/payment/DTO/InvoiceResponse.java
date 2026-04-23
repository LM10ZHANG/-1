package com.yourcompany.sales.modules.payment.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class InvoiceResponse {

    private String invoiceNo;
    private Long orderId;
    private BigDecimal invoiceAmount;
    private String invoiceStatus;
    private LocalDate invoiceDate;
}