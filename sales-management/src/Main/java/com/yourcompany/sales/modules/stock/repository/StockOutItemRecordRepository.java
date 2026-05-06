package com.yourcompany.sales.modules.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourcompany.sales.modules.stock.entity.StockOutItemRecord;

public interface StockOutItemRecordRepository extends JpaRepository<StockOutItemRecord, Long> {

    List<StockOutItemRecord> findByStockOutRecordId(Long stockOutRecordId);
}
