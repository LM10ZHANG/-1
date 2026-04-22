package com.yourcompany.sales.modules.quote.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 审批请求 DTO（用于通过/驳回操作）
 */
@Data
public class ApprovalRequest {

    @NotBlank(message = "审批意见不能为空")
    private String comment;
}