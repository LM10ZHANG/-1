package com.yourcompany.sales.modules.payment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.result.Result;
import com.yourcompany.sales.modules.payment.DTO.InvoiceRequest;
import com.yourcompany.sales.modules.payment.DTO.InvoiceResponse;
import com.yourcompany.sales.modules.payment.service.FinanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final FinanceService financeService;

    @GetMapping
    public Result<List<InvoiceResponse>> list(@RequestParam(required = false) Long orderId) {
        return Result.success(financeService.getInvoices(orderId));
    }

    @PostMapping
    public Result<Void> create(@RequestBody InvoiceRequest req) {
        financeService.createInvoice(req);
        return Result.success();
    }
}
