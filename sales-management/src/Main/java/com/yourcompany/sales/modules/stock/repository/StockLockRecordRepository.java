package com.yourcompany.sales.modules.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourcompany.sales.modules.stock.entity.StockLockRecord;

import java.util.List;

public interface StockLockRecordRepository
        extends JpaRepository<StockLockRecord, Long> {

    List<StockLockRecord> findByOrderId(Long orderId);

    List<StockLockRecord> findByOrderIdAndSkuId(Long orderId, Long skuId);

    List<StockLockRecord> findByOrderIdAndOrderItemId(Long orderId, Long orderItemId);

    void deleteByOrderId(Long orderId);
}
