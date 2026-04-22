package com.yourcompany.sales.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Long id;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private String skuNameSnapshot;
    private Integer qty;
    private Integer lockedQty;
    private Integer outboundQty;
    private BigDecimal unitPrice;
    private BigDecimal taxRate;
    private BigDecimal discountRate;
    private BigDecimal lineAmount;
    private String remark;
}