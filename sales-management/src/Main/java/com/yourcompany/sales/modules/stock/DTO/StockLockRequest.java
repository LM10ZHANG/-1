package com.yourcompany.sales.modules.stock.dto;

import java.util.List;

import lombok.Data;

@Data
public class StockLockRequest {
    private Long orderId;
    private Long warehouseId;
    private String remark;
    private List<StockItemDTO> items;
}

