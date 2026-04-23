package com.yourcompany.sales.modules.stock.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.result.Result;
import com.yourcompany.sales.modules.stock.DTO.ReturnRequest;
import com.yourcompany.sales.modules.stock.DTO.StockLockRequest;
import com.yourcompany.sales.modules.stock.entity.InventoryStock;
import com.yourcompany.sales.modules.stock.service.StockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public Result<List<InventoryStock>> list(@RequestParam(required = false) Long skuId) {
        return Result.success(stockService.list(skuId));
    }

    @PostMapping("/lock")
    public Result<Void> lock(@RequestBody StockLockRequest req) {
        stockService.lockStock(req);
        return Result.success();
    }

    @PostMapping("/release")
    public Result<Void> release(@RequestParam Long orderId) {
        stockService.releaseStock(orderId);
        return Result.success();
    }

    @PostMapping("/return")
    public Result<Void> returnInbound(@RequestBody ReturnRequest req) {
        stockService.returnInbound(req);
        return Result.success();
    }
}
