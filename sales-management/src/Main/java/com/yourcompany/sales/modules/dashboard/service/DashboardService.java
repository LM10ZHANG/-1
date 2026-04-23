package com.yourcompany.sales.modules.dashboard.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import com.yourcompany.sales.modules.dashboard.DTO.DashboardWarningsVO;
import com.yourcompany.sales.modules.dashboard.DTO.OverviewVO;
import com.yourcompany.sales.modules.dashboard.DTO.ProductRankingVO;
import com.yourcompany.sales.modules.dashboard.DTO.SalesTrendVO;
import com.yourcompany.sales.modules.order.dto.OrderResponse;
import com.yourcompany.sales.modules.stock.entity.InventoryStock;

public interface DashboardService {

    OverviewVO getOverview(LocalDateTime start, LocalDateTime end);

    List<ProductRankingVO> getProductRanking(LocalDateTime start, LocalDateTime end);

    List<InventoryStock> getLowStockWarnings();

    List<OrderResponse> getOverduePayments();

    DashboardWarningsVO getWarnings();

    List<SalesTrendVO> getSalesTrend(LocalDate start, LocalDate end);

}
