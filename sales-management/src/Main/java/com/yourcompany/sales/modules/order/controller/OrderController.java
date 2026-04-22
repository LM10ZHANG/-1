package com.yourcompany.sales.modules.order.controller;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.modules.order.dto.*;
import com.yourcompany.sales.modules.order.service.OrderService;
import com.yourcompany.sales.modules.order.service.OrderTimelineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderTimelineService timelineService;

    /**
     * 创建订单
     */
    @PostMapping
    public ApiResponse<OrderResponse> create(@Valid @RequestBody OrderCreateRequest request) {
        return ApiResponse.success(orderService.createOrder(request));
    }

    /**
     * 更新订单
     */
    @PutMapping("/{id}")
    public ApiResponse<OrderResponse> update(@PathVariable Long id,
                                             @Valid @RequestBody OrderUpdateRequest request) {
        return ApiResponse.success(orderService.updateOrder(id, request));
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(orderService.getOrderById(id));
    }

    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ApiResponse.success();
    }

    /**
     * 获取订单时间线
     */
    @GetMapping("/{id}/timeline")
    public ApiResponse<OrderTimelineVo> getTimeline(@PathVariable Long id) {
        return ApiResponse.success(timelineService.getTimeline(id));
    }

    // 可根据需要添加分页查询接口
}