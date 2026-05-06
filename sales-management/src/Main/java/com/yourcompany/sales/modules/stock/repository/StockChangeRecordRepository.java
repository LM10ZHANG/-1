package com.yourcompany.sales.modules.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourcompany.sales.modules.stock.entity.StockChangeRecord;

public interface StockChangeRecordRepository extends JpaRepository<StockChangeRecord, Long> {

    List<StockChangeRecord> findTop20ByStockIdOrderByCreatedAtDesc(Long stockId);
}
