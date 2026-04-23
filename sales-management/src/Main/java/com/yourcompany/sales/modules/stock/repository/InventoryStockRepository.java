package com.yourcompany.sales.modules.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourcompany.sales.modules.stock.entity.InventoryStock;

import java.util.List;
import java.util.Optional;

public interface InventoryStockRepository
        extends JpaRepository<InventoryStock, Long> {

    Optional<InventoryStock> findByWarehouseIdAndSkuId(Long warehouseId, Long skuId);

    List<InventoryStock> findByAvailableQtyLessThanEqual(Integer warnQty);

    List<InventoryStock> findBySkuId(Long skuId);
}
