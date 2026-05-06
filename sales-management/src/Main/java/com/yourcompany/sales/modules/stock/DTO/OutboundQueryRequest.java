package com.yourcompany.sales.modules.stock.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OutboundQueryRequest {

    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private Long orderId;
    private String outboundNo;
    private Long warehouseId;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
