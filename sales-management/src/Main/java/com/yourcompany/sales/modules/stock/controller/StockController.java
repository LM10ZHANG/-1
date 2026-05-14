package com.yourcompany.sales.modules.stock.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.common.result.Result;
import com.yourcompany.sales.modules.stock.dto.StockDetailResponse;
import com.yourcompany.sales.modules.stock.dto.StockLockRequest;
import com.yourcompany.sales.modules.stock.dto.StockQueryRequest;
import com.yourcompany.sales.modules.stock.dto.StockReleaseRequest;
import com.yourcompany.sales.modules.stock.dto.StockResponse;
import com.yourcompany.sales.modules.stock.service.StockService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    @PreAuthorize("hasAuthority('stock:list')")
    public Result<PageResponse<StockResponse>> list(StockQueryRequest req) {
        return Result.success(stockService.pageStocks(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('stock:detail')")
    public Result<StockDetailResponse> detail(@PathVariable Long id) {
        return Result.success(stockService.getStockDetail(id));
    }

    @PostMapping("/lock")
    @PreAuthorize("hasAuthority('stock:lock')")
    public Result<Void> lock(@RequestBody StockLockRequest req) {
        stockService.lockStock(req);
        return Result.success();
    }

    @PostMapping("/release")
    @PreAuthorize("hasAuthority('stock:release')")
    public Result<Void> release(@RequestBody StockReleaseRequest req) {
        stockService.releaseStock(req);
        return Result.success();
    }
}
