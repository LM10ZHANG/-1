package com.yourcompany.sales.modules.stock.dto;

import java.util.List;

import lombok.Data;

@Data
public class OutboundDetailResponse {

    private OutboundResponse header;
    private List<OutboundItemResponse> items;
}
