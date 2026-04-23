package com.yourcompany.sales.modules.customer.dto;

import com.yourcompany.sales.common.dto.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户分页查询请求 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerQueryRequest extends PageRequest {

    private String customerName;

    private String customerLevel;

    private String customerType;

    private String industry;

    private Long ownerUserId;

    private Integer status;
}
