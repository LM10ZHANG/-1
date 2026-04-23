package com.yourcompany.sales.modules.stock.DTO;

import lombok.Data;

@Data
public class StockItemDTO {
    private Long skuId;
    private Integer quantity;
}