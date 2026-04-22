package com.yourcompany.sales.common.enums;

import lombok.Getter;

/**
 * 审批状态枚举（用于单据自身的审批状态字段）
 */
@Getter
public enum ApprovalStatus {

    NOT_SUBMIT("未提交"),
    PENDING("待审批"),
    APPROVED("已通过"),
    REJECTED("已驳回");

    private final String description;

    ApprovalStatus(String description) {
        this.description = description;
    }
}