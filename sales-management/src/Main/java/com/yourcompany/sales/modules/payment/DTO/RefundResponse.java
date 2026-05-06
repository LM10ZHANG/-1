package com.yourcompany.sales.modules.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RefundResponse {

    private Long id;
    private String refundNo;
    private Long orderId;
    private Long paymentId;
    private BigDecimal refundAmount;
    private String refundReason;
    private LocalDateTime refundTime;
    private String status;
    private LocalDateTime createdAt;
}
