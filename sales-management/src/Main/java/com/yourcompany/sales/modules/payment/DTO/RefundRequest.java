package com.yourcompany.sales.modules.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RefundRequest {

    private Long orderId;
    private Long paymentId;

    private BigDecimal refundAmount;
    private String refundReason;
    private LocalDateTime refundTime;
}
