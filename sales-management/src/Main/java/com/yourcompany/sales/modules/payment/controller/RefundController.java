package com.yourcompany.sales.modules.payment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.result.Result;
import com.yourcompany.sales.modules.payment.DTO.RefundRequest;
import com.yourcompany.sales.modules.payment.entity.RefundRecord;
import com.yourcompany.sales.modules.payment.service.FinanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final FinanceService financeService;

    @PostMapping
    public Result<Void> create(@RequestBody RefundRequest req) {
        financeService.createRefund(req);
        return Result.success();
    }

    @PostMapping("/{id}/finish")
    public Result<Void> finish(@PathVariable Long id) {
        financeService.finishRefund(id);
        return Result.success();
    }

    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id) {
        financeService.rejectRefund(id);
        return Result.success();
    }

    @GetMapping
    public Result<List<RefundRecord>> list(@RequestParam(required = false) Long orderId) {
        return Result.success(financeService.getRefunds(orderId));
    }
}
