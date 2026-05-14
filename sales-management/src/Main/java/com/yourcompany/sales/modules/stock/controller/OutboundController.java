package com.yourcompany.sales.modules.stock.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.common.result.Result;
import com.yourcompany.sales.modules.stock.dto.OutboundDetailResponse;
import com.yourcompany.sales.modules.stock.dto.OutboundQueryRequest;
import com.yourcompany.sales.modules.stock.dto.OutboundRequest;
import com.yourcompany.sales.modules.stock.dto.OutboundResponse;
import com.yourcompany.sales.modules.stock.dto.ReturnRequest;
import com.yourcompany.sales.modules.stock.service.StockService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequiredArgsConstructor
public class OutboundController {

    private final StockService stockService;

    @PostMapping("/api/outbound-orders")
    @PreAuthorize("hasAuthority('outbound:create')")
    public Result<Void> outbound(@RequestBody OutboundRequest req) {
        stockService.outbound(req);
        return Result.success();
    }

    @GetMapping("/api/outbound-orders")
    @PreAuthorize("hasAuthority('outbound:list')")
    public Result<PageResponse<OutboundResponse>> pageOutboundOrders(OutboundQueryRequest req) {
        return Result.success(stockService.pageOutboundOrders(req));
    }

    @GetMapping("/api/outbound-orders/{id}")
    @PreAuthorize("hasAuthority('outbound:detail')")
    public Result<OutboundDetailResponse> outboundDetail(@PathVariable Long id) {
        return Result.success(stockService.getOutboundDetail(id));
    }

    @PostMapping("/api/returns/inbound")
    @PreAuthorize("hasAuthority('stock:return:inbound')")
    public Result<Void> returnInbound(@RequestBody ReturnRequest req) {
        stockService.returnInbound(req);
        return Result.success();
    }
}
