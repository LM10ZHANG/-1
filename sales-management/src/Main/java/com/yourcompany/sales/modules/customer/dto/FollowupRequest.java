package com.yourcompany.sales.modules.customer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 新增跟进记录请求 DTO
 */
@Data
public class FollowupRequest {

    @NotBlank(message = "跟进方式不能为空")
    private String followType;               // PHONE/VISIT/WECHAT/EMAIL

    @NotBlank(message = "跟进内容不能为空")
    private String content;

    private LocalDateTime nextFollowTime;

    private String followResult;
}
