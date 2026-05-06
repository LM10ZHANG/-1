package com.yourcompany.sales.modules.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class InvoiceRequest {

    private Long orderId;

    private String invoiceTitle;
    private String taxNo;

    private BigDecimal invoiceAmount;
    private String invoiceStatus;
    private LocalDate invoiceDate;
}
