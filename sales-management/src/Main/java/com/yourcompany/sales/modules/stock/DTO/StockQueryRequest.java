package com.yourcompany.sales.modules.stock.dto;

import lombok.Data;

@Data
public class StockQueryRequest {

    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private Long warehouseId;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private Boolean lowStockOnly;
}
