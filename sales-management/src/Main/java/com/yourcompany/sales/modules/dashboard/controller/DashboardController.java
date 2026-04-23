package com.yourcompany.sales.modules.dashboard.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.modules.dashboard.DTO.DashboardWarningsVO;
import com.yourcompany.sales.modules.dashboard.DTO.OverviewVO;
import com.yourcompany.sales.modules.dashboard.DTO.ProductRankingVO;
import com.yourcompany.sales.modules.dashboard.DTO.SalesTrendVO;
import com.yourcompany.sales.modules.dashboard.service.DashboardService;
import com.yourcompany.sales.modules.order.dto.OrderResponse;
import com.yourcompany.sales.modules.stock.entity.InventoryStock;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public ApiResponse<OverviewVO> overview(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {
        return ApiResponse.success(dashboardService.getOverview(start, end));
    }

    @GetMapping("/rankings")
    public ApiResponse<List<ProductRankingVO>> rankings(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {
        return ApiResponse.success(dashboardService.getProductRanking(start, end));
    }

    @GetMapping("/warnings")
    public ApiResponse<DashboardWarningsVO> warnings() {
        return ApiResponse.success(dashboardService.getWarnings());
    }

    @GetMapping("/warnings/low-stock")
    public ApiResponse<List<InventoryStock>> lowStockWarnings() {
        return ApiResponse.success(dashboardService.getLowStockWarnings());
    }

    @GetMapping("/warnings/overdue-payments")
    public ApiResponse<List<OrderResponse>> overduePayments() {
        return ApiResponse.success(dashboardService.getOverduePayments());
    }

    @GetMapping("/sales-trend")
    public ApiResponse<List<SalesTrendVO>> salesTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return ApiResponse.success(dashboardService.getSalesTrend(start, end));
    }
}
