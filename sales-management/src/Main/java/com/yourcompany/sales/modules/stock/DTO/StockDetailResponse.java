package com.yourcompany.sales.modules.stock.dto;

import java.util.List;

import lombok.Data;

@Data
public class StockDetailResponse {

    private StockResponse stock;
    private List<StockChangeResponse> changeRecords;
}
