package com.yourcompany.sales.modules.dashboard.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.modules.dashboard.dto.SalesTrendVO;
import com.yourcompany.sales.modules.dashboard.dto.DashboardQuery;
import com.yourcompany.sales.modules.dashboard.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final DashboardService dashboardService;

    @GetMapping("/sales-trend")
    public ApiResponse<List<SalesTrendVO>> salesTrend(DashboardQuery query) {
        return ApiResponse.success(dashboardService.getSalesTrend(query));
    }
}
