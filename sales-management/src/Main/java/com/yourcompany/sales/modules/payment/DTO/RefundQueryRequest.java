package com.yourcompany.sales.modules.payment.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RefundQueryRequest {

    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private Long orderId;
    private Long paymentId;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
