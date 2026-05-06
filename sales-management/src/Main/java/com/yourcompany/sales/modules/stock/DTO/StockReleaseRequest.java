package com.yourcompany.sales.modules.stock.dto;

import java.util.List;

import lombok.Data;

@Data
public class StockReleaseRequest {

    private Long orderId;
    private Long warehouseId;
    private String reason;
    private List<StockItemDTO> items;
}
