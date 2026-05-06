package com.yourcompany.sales.modules.dashboard.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DashboardOverviewVO {

    private Long pendingApprovalQuotes;
    private Long pendingOutboundOrders;
    private Long pendingReceivableOrders;
    private Long todayNewCustomers;

    private BigDecimal monthOrderAmount;
    private BigDecimal monthPaymentAmount;
    private BigDecimal orderCompletionRate;

    private String roleView;
}
