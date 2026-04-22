package com.yourcompany.sales.modules.order.service;

import com.yourcompany.sales.modules.order.dto.OrderTimelineVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderTimelineService {

    private final OrderService orderService;

    public OrderTimelineVo getTimeline(Long orderId) {
        return orderService.getOrderTimeline(orderId);
    }
}