package com.yourcompany.sales.common.enums;

import lombok.Getter;

/**
 * 订单支付状态
 */
@Getter
public enum PaymentStatus {

    UNPAID("未支付"),
    PARTIAL("部分支付"),
    PAID("已支付"),
    REFUNDED("已退款");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }
}