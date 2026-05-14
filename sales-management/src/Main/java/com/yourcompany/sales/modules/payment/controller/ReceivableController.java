package com.yourcompany.sales.modules.payment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.common.result.Result;
import com.yourcompany.sales.modules.payment.dto.ReceivableQueryRequest;
import com.yourcompany.sales.modules.payment.dto.ReceivableResponse;
import com.yourcompany.sales.modules.payment.service.FinanceService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/receivables")
@RequiredArgsConstructor
public class ReceivableController {

    private final FinanceService financeService;

    @GetMapping
    @PreAuthorize("hasAuthority('receivable:list')")
    public Result<PageResponse<ReceivableResponse>> list(ReceivableQueryRequest req) {
        return Result.success(financeService.pageReceivables(req));
    }
}
