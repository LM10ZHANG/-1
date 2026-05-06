package com.yourcompany.sales.modules.stock.dto;

import lombok.Data;

@Data
public class OutboundItemResponse {

    private Long id;
    private Long orderItemId;
    private Long skuId;
    private Integer outboundQty;
}
