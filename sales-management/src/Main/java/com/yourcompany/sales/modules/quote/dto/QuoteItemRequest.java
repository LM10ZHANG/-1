package com.yourcompany.sales.modules.quote.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 报价明细请求 DTO
 */
@Data
public class QuoteItemRequest {

    @NotNull(message = "SKU ID不能为空")
    private Long skuId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer qty;

    private BigDecimal discountRate = BigDecimal.ONE;  // 默认无折扣

    private String remark;
}