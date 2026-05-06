package com.yourcompany.sales.modules.payment.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PaymentQueryRequest {

    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private Long orderId;
    private Long customerId;
    private String paymentNo;
    private String payMethod;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
