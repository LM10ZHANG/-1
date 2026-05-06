package com.yourcompany.sales.modules.stock.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "stock_change_record")
public class StockChangeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long stockId;
    private Long warehouseId;
    private Long skuId;
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
