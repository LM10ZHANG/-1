package com.yourcompany.sales.modules.stock.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity
@Table(name = "inventory_stock",
       uniqueConstraints = @UniqueConstraint(columnNames = {"warehouse_id", "sku_id"}))
public class InventoryStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long warehouseId;
    private Long skuId;

    private Integer totalQty;
    private Integer availableQty;
    private Integer lockedQty;
    private Integer warnQty;

    private LocalDateTime updatedAt;
}