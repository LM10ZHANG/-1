package com.yourcompany.sales.modules.product.dto;

import com.yourcompany.sales.common.dto.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SPU 分页查询请求 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SpuQueryRequest extends PageRequest {

    private String spuName;

    private Long categoryId;

    private Integer status;
}
