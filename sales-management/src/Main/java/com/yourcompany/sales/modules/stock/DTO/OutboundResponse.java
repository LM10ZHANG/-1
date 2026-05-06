package com.yourcompany.sales.modules.stock.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OutboundResponse {

    private Long id;
    private String outboundNo;
    private Long orderId;
    private Long warehouseId;
    private String status;
    private String remark;
    private LocalDateTime outboundTime;
}
