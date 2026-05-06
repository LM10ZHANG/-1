package com.yourcompany.sales.modules.stock.dto;

import lombok.Data;

@Data
public class StockItemDTO {
    private Long orderItemId;
    private Long skuId;
    private Integer quantity;
}
