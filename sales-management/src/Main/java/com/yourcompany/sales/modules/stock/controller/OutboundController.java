package com.yourcompany.sales.modules.stock.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.result.Result;
import com.yourcompany.sales.modules.stock.DTO.OutboundRequest;
import com.yourcompany.sales.modules.stock.service.StockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/outbound-orders")
@RequiredArgsConstructor
public class OutboundController {

    private final StockService stockService;

    /**
     * 创建出库单 + 执行出库
     */
    @PostMapping
    public Result<Void> outbound(@RequestBody OutboundRequest req) {
        stockService.outbound(req);
        return Result.success();
    }
}