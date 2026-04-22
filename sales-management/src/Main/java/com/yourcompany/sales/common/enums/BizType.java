package com.yourcompany.sales.common.enums;

import lombok.Getter;

/**
 * 业务类型枚举（用于审批记录、操作日志的 bizType 字段）
 */
@Getter
public enum BizType {

    QUOTE("报价单"),
    ORDER("销售订单"),
    REFUND("退款单");

    private final String description;

    BizType(String description) {
        this.description = description;
    }
}