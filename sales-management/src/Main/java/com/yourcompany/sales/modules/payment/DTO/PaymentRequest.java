package com.yourcompany.sales.modules.payment.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaymentRequest {

    private Long orderId;
    private Long customerId;

    private BigDecimal payAmount;
    private String payMethod;

    private String voucherUrl;
    private Long operatorUserId;

    private String remark;
}