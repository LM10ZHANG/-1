package com.yourcompany.sales.modules.dashboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.modules.dashboard.dto.DashboardOverviewVO;
import com.yourcompany.sales.modules.dashboard.dto.DashboardQuery;
import com.yourcompany.sales.modules.dashboard.dto.DashboardWarningsVO;
import com.yourcompany.sales.modules.dashboard.dto.RankingItemVO;
import com.yourcompany.sales.modules.dashboard.enums.RankingType;
import com.yourcompany.sales.modules.dashboard.service.DashboardService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    @PreAuthorize("hasAuthority('dashboard:overview')")
    public ApiResponse<DashboardOverviewVO> overview(DashboardQuery query) {
        return ApiResponse.success(dashboardService.getOverview(query));
    }

    @GetMapping("/rankings")
    @PreAuthorize("hasAuthority('dashboard:rankings')")
    public ApiResponse<PageResponse<RankingItemVO>> rankings(
            DashboardQuery query,
            @RequestParam RankingType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(dashboardService.getRankings(query, type, page, size));
    }

    @GetMapping("/warnings")
    @PreAuthorize("hasAuthority('dashboard:warnings')")
    public ApiResponse<DashboardWarningsVO> warnings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(dashboardService.getWarnings(page, size));
    }
}
