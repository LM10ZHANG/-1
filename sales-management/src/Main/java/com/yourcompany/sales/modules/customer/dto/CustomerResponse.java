package com.yourcompany.sales.modules.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long id;

    private String customerCode;

    private String customerName;

    private String customerLevel;

    private String customerType;

    private String industry;

    private String source;

    private String province;

    private String city;

    private String address;

    private Long ownerUserId;

    private BigDecimal creditLimit;

    private BigDecimal currentArAmount;

    private String followStatus;

    private Integer status;

    private String remark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /** 详情接口会填充该字段；列表接口为空 */
    private List<ContactResponse> contacts;
}
