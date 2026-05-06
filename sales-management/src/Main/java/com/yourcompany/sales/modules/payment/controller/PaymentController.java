package com.yourcompany.sales.modules.payment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.common.result.Result;
import com.yourcompany.sales.modules.payment.dto.PaymentQueryRequest;
import com.yourcompany.sales.modules.payment.dto.PaymentRequest;
import com.yourcompany.sales.modules.payment.dto.PaymentResponse;
import com.yourcompany.sales.modules.payment.service.FinanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final FinanceService financeService;

    @PostMapping
    public Result<Void> create(@RequestBody PaymentRequest req) {
        financeService.createPayment(req);
        return Result.success();
    }

    @GetMapping
    public Result<PageResponse<PaymentResponse>> list(PaymentQueryRequest req) {
        return Result.success(financeService.pagePayments(req));
    }
}
