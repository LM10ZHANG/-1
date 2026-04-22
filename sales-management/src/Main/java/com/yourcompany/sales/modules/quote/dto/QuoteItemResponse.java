package com.yourcompany.sales.modules.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 报价明细响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteItemResponse {

    private Long id;

    private Long skuId;

    private String skuCode;             // SKU编码

    private String skuName;             // SKU名称（当前名称，快照另存）

    private String skuNameSnapshot;     // 报价时名称快照

    private Integer qty;

    private BigDecimal originUnitPrice;

    private BigDecimal discountRate;

    private BigDecimal dealUnitPrice;

    private BigDecimal taxRate;

    private BigDecimal lineAmount;

    private String remark;
}