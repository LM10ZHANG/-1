package com.yourcompany.sales.modules.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 新增客户请求 DTO
 */
@Data
public class CustomerCreateRequest {

    @NotBlank(message = "客户编码不能为空")
    @Size(max = 50, message = "客户编码长度不能超过 50")
    private String customerCode;

    @NotBlank(message = "客户名称不能为空")
    @Size(max = 120, message = "客户名称长度不能超过 120")
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

    private String followStatus;

    private String remark;
}
