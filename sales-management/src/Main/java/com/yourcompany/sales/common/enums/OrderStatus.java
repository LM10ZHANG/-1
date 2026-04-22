package com.yourcompany.sales.common.enums;

import lombok.Getter;

/**
 * 销售订单状态枚举
 */
@Getter
public enum OrderStatus {

    DRAFT("草稿"),
    PENDING_APPROVAL("待审批"),
    WAIT_STOCK("待备货"),
    WAIT_OUTBOUND("待出库"),
    OUTBOUND("已出库"),
    PARTIAL_PAID("部分收款"),
    COMPLETED("已完成"),
    CANCELLED("已取消"),
    CLOSED("已关闭");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    /**
     * 是否允许取消订单
     */
    public boolean canCancel() {
        return this != COMPLETED && this != CANCELLED && this != CLOSED;
    }

    /**
     * 是否允许进行出库操作
     */
    public boolean canOutbound() {
        return this == WAIT_OUTBOUND;
    }

    /**
     * 是否为终态（订单生命周期结束）
     */
    public boolean isFinal() {
        return this == COMPLETED || this == CANCELLED || this == CLOSED;
    }
}