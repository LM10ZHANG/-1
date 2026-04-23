package com.yourcompany.sales.modules.product.dto;

import com.yourcompany.sales.common.dto.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SKU 分页查询请求 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SkuQueryRequest extends PageRequest {

    private Long spuId;

    private String skuName;

    private String skuCode;

    private String barcode;

    private Integer status;
}
