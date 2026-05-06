package com.yourcompany.sales.modules.stock.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StockResponse {

    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private Integer totalQty;
    private Integer availableQty;
    private Integer lockedQty;
    private Integer warnQty;
    private Boolean lowStock;
    private LocalDateTime updatedAt;
}
