package com.yourcompany.sales.modules.payment.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class InvoiceRequest {

    private Long orderId;

    private String invoiceTitle;
    private String taxNo;

    private BigDecimal invoiceAmount;
}