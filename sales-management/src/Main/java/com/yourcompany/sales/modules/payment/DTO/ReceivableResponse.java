package com.yourcompany.sales.modules.payment.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ReceivableResponse {

    private BigDecimal totalAmount;
    private BigDecimal receivedAmount;
    private BigDecimal unreceivedAmount;
    private Long overdueDays;
}
