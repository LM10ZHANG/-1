package com.yourcompany.sales.modules.dashboard.DTO;

import java.util.List;

import com.yourcompany.sales.modules.dashboard.entity.NotificationMessage;
import com.yourcompany.sales.modules.order.dto.OrderResponse;
import com.yourcompany.sales.modules.stock.entity.InventoryStock;

import lombok.Data;

@Data
public class DashboardWarningsVO {

    private List<InventoryStock> lowStockWarnings;
    private List<OrderResponse> overduePayments;
    private List<NotificationMessage> unreadMessages;
}
