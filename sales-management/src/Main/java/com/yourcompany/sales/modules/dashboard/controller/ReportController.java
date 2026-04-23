package com.yourcompany.sales.modules.dashboard.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.modules.dashboard.DTO.SalesTrendVO;
import com.yourcompany.sales.modules.dashboard.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final DashboardService dashboardService;

    @GetMapping("/sales-trend")
    public ApiResponse<List<SalesTrendVO>> salesTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return ApiResponse.success(dashboardService.getSalesTrend(start, end));
    }
}