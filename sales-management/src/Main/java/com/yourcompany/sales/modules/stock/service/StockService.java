package com.yourcompany.sales.modules.stock.service;

import java.util.List;

import com.yourcompany.sales.modules.stock.DTO.OutboundRequest;
import com.yourcompany.sales.modules.stock.DTO.ReturnRequest;
import com.yourcompany.sales.modules.stock.DTO.StockLockRequest;
import com.yourcompany.sales.modules.stock.entity.InventoryStock;

public interface StockService {

    List<InventoryStock> list(Long skuId);

    void lockStock(StockLockRequest req);

    void releaseStock(Long orderId);

    void outbound(OutboundRequest req);

    void returnInbound(ReturnRequest req);

    void checkAndCreateWarn(InventoryStock stock);
}