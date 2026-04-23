package com.yourcompany.sales.modules.dashboard.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRankingVO {

    private Long skuId;           // 商品ID
    private Integer totalQty;     // 总销量
    private BigDecimal totalAmount; // 销售额

    public ProductRankingVO(Long skuId, Integer totalQty) {
        this.skuId = skuId;
        this.totalQty = totalQty;
        this.totalAmount = BigDecimal.ZERO;
    }

    public Integer getQty() {
        return totalQty;
    }

    public void setQty(Integer qty) {
        this.totalQty = qty;
    }
}
