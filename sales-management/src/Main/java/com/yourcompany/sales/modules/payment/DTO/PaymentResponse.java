package com.yourcompany.sales.modules.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PaymentResponse {

    private Long id;
    private String paymentNo;
    private Long orderId;
    private String orderNo;
    private Long customerId;
    private String customerName;
    private BigDecimal payAmount;
    private String payMethod;
    private LocalDateTime payTime;
    private String voucherUrl;
    private Long operatorUserId;
    private String status;
    private String remark;
}
