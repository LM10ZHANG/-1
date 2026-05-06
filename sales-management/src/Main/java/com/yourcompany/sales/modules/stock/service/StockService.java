package com.yourcompany.sales.modules.stock.service;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.modules.stock.dto.OutboundDetailResponse;
import com.yourcompany.sales.modules.stock.dto.OutboundQueryRequest;
import com.yourcompany.sales.modules.stock.dto.OutboundRequest;
import com.yourcompany.sales.modules.stock.dto.OutboundResponse;
import com.yourcompany.sales.modules.stock.dto.ReturnRequest;
import com.yourcompany.sales.modules.stock.dto.StockDetailResponse;
import com.yourcompany.sales.modules.stock.dto.StockLockRequest;
import com.yourcompany.sales.modules.stock.dto.StockQueryRequest;
import com.yourcompany.sales.modules.stock.dto.StockReleaseRequest;
import com.yourcompany.sales.modules.stock.dto.StockResponse;
import com.yourcompany.sales.modules.stock.entity.InventoryStock;

public interface StockService {

    PageResponse<StockResponse> pageStocks(StockQueryRequest req);

    StockDetailResponse getStockDetail(Long stockId);

    void lockStock(StockLockRequest req);

    void releaseStock(StockReleaseRequest req);

    void outbound(OutboundRequest req);

    PageResponse<OutboundResponse> pageOutboundOrders(OutboundQueryRequest req);

    OutboundDetailResponse getOutboundDetail(Long id);

    void returnInbound(ReturnRequest req);

    void checkAndCreateWarn(InventoryStock stock);
}
