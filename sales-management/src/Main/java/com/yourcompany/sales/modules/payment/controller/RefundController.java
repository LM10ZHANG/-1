package com.yourcompany.sales.modules.payment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.common.result.Result;
import com.yourcompany.sales.modules.payment.dto.RefundQueryRequest;
import com.yourcompany.sales.modules.payment.dto.RefundRequest;
import com.yourcompany.sales.modules.payment.dto.RefundResponse;
import com.yourcompany.sales.modules.payment.service.FinanceService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final FinanceService financeService;

    @PostMapping
    @PreAuthorize("hasAuthority('refund:create')")
    public Result<Void> create(@RequestBody RefundRequest req) {
        financeService.createRefund(req);
        return Result.success();
    }

    @PostMapping("/{id}/finish")
    @PreAuthorize("hasAuthority('refund:finish')")
    public Result<Void> finish(@PathVariable Long id) {
        financeService.finishRefund(id);
        return Result.success();
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('refund:reject')")
    public Result<Void> reject(@PathVariable Long id) {
        financeService.rejectRefund(id);
        return Result.success();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('refund:list')")
    public Result<PageResponse<RefundResponse>> list(RefundQueryRequest req) {
        return Result.success(financeService.pageRefunds(req));
    }
}
