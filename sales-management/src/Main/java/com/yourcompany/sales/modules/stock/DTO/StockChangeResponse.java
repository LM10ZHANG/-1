package com.yourcompany.sales.modules.stock.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StockChangeResponse {

    private Long id;
    private String changeType;
    private Integer beforeTotalQty;
    private Integer beforeAvailableQty;
    private Integer beforeLockedQty;
    private Integer changeQty;
    private Integer afterTotalQty;
    private Integer afterAvailableQty;
    private Integer afterLockedQty;
    private String bizType;
    private Long bizId;
    private String remark;
    private Long operatorUserId;
    private LocalDateTime createdAt;
}
