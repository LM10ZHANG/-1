package com.yourcompany.sales.modules.payment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.common.result.Result;
import com.yourcompany.sales.modules.payment.dto.InvoiceQueryRequest;
import com.yourcompany.sales.modules.payment.dto.InvoiceRequest;
import com.yourcompany.sales.modules.payment.dto.InvoiceResponse;
import com.yourcompany.sales.modules.payment.service.FinanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final FinanceService financeService;

    @GetMapping
    public Result<PageResponse<InvoiceResponse>> list(InvoiceQueryRequest req) {
        return Result.success(financeService.pageInvoices(req));
    }

    @PostMapping
    public Result<Void> create(@RequestBody InvoiceRequest req) {
        financeService.createInvoice(req);
        return Result.success();
    }
}
