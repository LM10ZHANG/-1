package com.yourcompany.sales.modules.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ReceivableResponse {

    private Long orderId;
    private String orderNo;
    private Long customerId;
    private String customerName;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private BigDecimal totalAmount;
    private BigDecimal receivedAmount;
    private BigDecimal unreceivedAmount;
    private String paymentStatus;
    private Long overdueDays;
    private String riskLevel;
}
