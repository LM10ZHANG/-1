package com.yourcompany.sales.modules.dashboard.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OverviewVO {

    // 订单维度
    private Long totalOrders;              // 订单总数
    private BigDecimal totalOrderAmount;  // 订单总金额

    // 收款维度
    private BigDecimal totalPayment;      // 已收款金额

    // 比率
    private Double completionRate;        // 订单完成率（0-1）

    // 待办
    private Long pendingOutbound;         // 待出库订单数
    private Long pendingPayment;          // 待收款订单数
}