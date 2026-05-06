package com.yourcompany.sales.modules.stock.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ReturnRequest {

    private Long orderId;
    private Long warehouseId;
    private LocalDateTime inboundTime;
    private String reason;
    private String remark;

    private List<StockItemDTO> items;
}
