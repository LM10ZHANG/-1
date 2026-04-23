package com.yourcompany.sales.modules.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SKU 响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuResponse {

    private Long id;

    private Long spuId;

    private String spuName;

    private String skuCode;

    private String skuName;

    private String specJson;

    private String barcode;

    private BigDecimal salePrice;

    private BigDecimal costPrice;

    private BigDecimal taxRate;

    private Integer stockWarnQty;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
