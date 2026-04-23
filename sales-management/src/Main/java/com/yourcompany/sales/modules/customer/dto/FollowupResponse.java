package com.yourcompany.sales.modules.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 跟进记录响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowupResponse {

    private Long id;

    private Long customerId;

    private Long followUserId;

    private String followType;

    private String content;

    private LocalDateTime nextFollowTime;

    private String followResult;

    private LocalDateTime createdAt;
}
