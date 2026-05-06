package com.yourcompany.sales.modules.dashboard.dto;

import java.util.List;

import lombok.Data;

@Data
public class DashboardWarningsVO {

    private List<WarningItemVO> overdueFollowCustomers;
    private List<WarningItemVO> lowStockWarnings;
    private List<WarningItemVO> overdueReceivables;
}
