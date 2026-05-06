package com.yourcompany.sales.modules.payment.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class InvoiceQueryRequest {

    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private Long orderId;
    private String invoiceNo;
    private String invoiceStatus;
    private LocalDate startDate;
    private LocalDate endDate;
}
