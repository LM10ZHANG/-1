package com.yourcompany.sales.modules.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 联系人响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponse {

    private Long id;

    private Long customerId;

    private String name;

    private String mobile;

    private String email;

    private String position;

    private String wechat;

    private Integer isPrimary;

    private String remark;

    private LocalDateTime createdAt;
}
