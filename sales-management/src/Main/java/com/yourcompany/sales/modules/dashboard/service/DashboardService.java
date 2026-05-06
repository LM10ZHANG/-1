package com.yourcompany.sales.modules.dashboard.service;

import java.util.List;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.modules.dashboard.dto.DashboardOverviewVO;
import com.yourcompany.sales.modules.dashboard.dto.DashboardQuery;
import com.yourcompany.sales.modules.dashboard.dto.DashboardWarningsVO;
import com.yourcompany.sales.modules.dashboard.dto.RankingItemVO;
import com.yourcompany.sales.modules.dashboard.dto.SalesTrendVO;
import com.yourcompany.sales.modules.dashboard.enums.RankingType;

public interface DashboardService {

    DashboardOverviewVO getOverview(DashboardQuery query);

    PageResponse<RankingItemVO> getRankings(DashboardQuery query, RankingType type, int page, int size);

    DashboardWarningsVO getWarnings(int page, int size);

    List<SalesTrendVO> getSalesTrend(DashboardQuery query);
}
