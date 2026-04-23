package com.yourcompany.sales.modules.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourcompany.sales.modules.stock.entity.StockLockRecord;

import java.util.List;

public interface StockLockRecordRepository
        extends JpaRepository<StockLockRecord, Long> {

    List<StockLockRecord> findByOrderId(Long orderId);

    List<StockLockRecord> findByOrderIdAndSkuId(Long orderId, Long skuId);

    void deleteByOrderId(Long orderId);

    void deleteByOrderIdAndSkuId(Long orderId, Long skuId);
}
