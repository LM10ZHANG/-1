package com.yourcompany.sales.modules.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * SKU 新增/更新请求 DTO
 */
@Data
public class SkuRequest {

    @NotNull(message = "所属 SPU 不能为空")
    private Long spuId;

    @NotBlank(message = "SKU 编码不能为空")
    @Size(max = 50)
    private String skuCode;

    @NotBlank(message = "SKU 名称不能为空")
    @Size(max = 120)
    private String skuName;

    private String specJson;

    private String barcode;

    @DecimalMin(value = "0.00", message = "销售价不能为负数")
    private BigDecimal salePrice = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "成本价不能为负数")
    private BigDecimal costPrice = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "税率不能为负数")
    private BigDecimal taxRate = BigDecimal.ZERO;

    private Integer stockWarnQty = 0;

    /** 默认启用 */
    private Integer status = 1;
}
