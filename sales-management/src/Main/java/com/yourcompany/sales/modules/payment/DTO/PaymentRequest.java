package com.yourcompany.sales.modules.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PaymentRequest {

    private Long orderId;
    private Long customerId;

    private BigDecimal payAmount;
    private String payMethod;
    private LocalDateTime payTime;

    private String voucherUrl;
    private Long operatorUserId;

    private String remark;
}
