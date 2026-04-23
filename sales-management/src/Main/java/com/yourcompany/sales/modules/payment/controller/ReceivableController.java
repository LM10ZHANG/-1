package com.yourcompany.sales.modules.payment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.result.Result;
import com.yourcompany.sales.modules.payment.DTO.ReceivableResponse;
import com.yourcompany.sales.modules.payment.service.FinanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/receivables")
@RequiredArgsConstructor
public class ReceivableController {

    private final FinanceService financeService;

    @GetMapping
    public Result<?> list(@RequestParam(required = false) Long orderId) {
        if (orderId != null) {
            return Result.success(financeService.getReceivable(orderId));
        }
        List<ReceivableResponse> receivables = financeService.getReceivables();
        return Result.success(receivables);
    }
}
