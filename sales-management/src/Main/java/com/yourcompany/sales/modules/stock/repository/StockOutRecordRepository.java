package com.yourcompany.sales.modules.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourcompany.sales.modules.stock.entity.StockOutRecord;

public interface StockOutRecordRepository
        extends JpaRepository<StockOutRecord, Long> {

    List<StockOutRecord> findByOrderId(Long orderId);
}
