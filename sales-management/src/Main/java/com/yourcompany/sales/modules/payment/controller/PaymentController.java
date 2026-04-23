package com.yourcompany.sales.modules.payment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.result.Result;
import com.yourcompany.sales.modules.payment.DTO.PaymentRequest;
import com.yourcompany.sales.modules.payment.entity.PaymentRecord;
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
    public Result<List<PaymentRecord>> list(@RequestParam(required = false) Long orderId) {
        return Result.success(financeService.getPayments(orderId));
    }
}
