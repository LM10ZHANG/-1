package com.yourcompany.sales.modules.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * SKU 定价快照 —— 给后端 C 的报价/订单创建时做明细快照使用。
 *
 * 目标：替换后端 C 当前硬编码在 QuoteService/OrderService 中的
 *   - skuNameSnapshot
 *   - originUnitPrice / unitPrice
 *   - taxRate
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuPricingVo {

    private Long skuId;

    private String skuName;

    private BigDecimal salePrice;

    private BigDecimal taxRate;

    private Integer status;       // 1 启用 / 0 停用
}
