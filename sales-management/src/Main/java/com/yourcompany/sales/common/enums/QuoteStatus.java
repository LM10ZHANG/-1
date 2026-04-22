package com.yourcompany.sales.common.enums;

import lombok.Getter;

/**
 * 报价单状态枚举
 */
@Getter
public enum QuoteStatus {

    DRAFT("草稿"),
    PENDING_APPROVAL("待审批"),
    APPROVED("已审批"),
    REJECTED("已驳回"),
    VOID("已作废"),
    CONVERTED("已转订单");

    private final String description;

    QuoteStatus(String description) {
        this.description = description;
    }

    /**
     * 判断当前状态是否允许编辑
     */
    public boolean isEditable() {
        return this == DRAFT || this == REJECTED;
    }

    /**
     * 判断当前状态是否可以提交审批
     */
    public boolean canSubmitApproval() {
        return this == DRAFT || this == REJECTED;
    }

    /**
     * 判断是否为终态（不可再修改或操作）
     */
    public boolean isFinal() {
        return this == VOID || this == CONVERTED;
    }
}