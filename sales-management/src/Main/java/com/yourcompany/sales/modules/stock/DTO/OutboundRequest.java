package com.yourcompany.sales.modules.stock.DTO;

import java.util.List;

import lombok.Data;

@Data
public class OutboundRequest {
    private Long orderId;
    private Long warehouseId;
    private List<StockItemDTO> items;
}