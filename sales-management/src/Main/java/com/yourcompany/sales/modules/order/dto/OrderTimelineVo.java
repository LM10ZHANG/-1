package com.yourcompany.sales.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 订单时间线视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTimelineVo {

    private Long orderId;
    private String orderNo;
    private List<OrderEventVo> events;
}