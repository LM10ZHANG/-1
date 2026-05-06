package com.yourcompany.sales.modules.payment.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReceivableQueryRequest {

    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private Long customerId;
    private Long orderId;
    private String paymentStatus;
    private Boolean overdueOnly;
    private LocalDate startDate;
    private LocalDate endDate;
}
