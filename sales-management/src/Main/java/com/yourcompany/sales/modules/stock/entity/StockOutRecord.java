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
@Table(name = "stock_out_record")
public class StockOutRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String outboundNo;
    private Long orderId;
    private Long warehouseId;
    private Long operatorUserId;

    private String status;
    private String remark;

    private LocalDateTime outboundTime;
    private LocalDateTime createdAt;
}