package com.yourcompany.sales.modules.payment.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class RefundRequest {

    private Long orderId;
    private Long paymentId;

    private BigDecimal refundAmount;
    private String refundReason;
}