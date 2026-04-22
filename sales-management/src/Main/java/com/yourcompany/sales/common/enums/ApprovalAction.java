package com.yourcompany.sales.common.enums;

import lombok.Getter;

/**
 * 审批动作（审批人对单据的操作）
 */
@Getter
public enum ApprovalAction {

    APPROVE("通过"),
    REJECT("驳回");

    private final String description;

    ApprovalAction(String description) {
        this.description = description;
    }
}