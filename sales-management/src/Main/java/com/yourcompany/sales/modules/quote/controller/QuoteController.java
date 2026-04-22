package com.yourcompany.sales.modules.quote.controller;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.modules.order.entity.SalesOrder;
import com.yourcompany.sales.modules.order.dto.OrderResponse;
import com.yourcompany.sales.modules.quote.dto.*;
import com.yourcompany.sales.modules.quote.service.QuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 报价单控制器
 */
@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    /**
     * 创建报价单
     */
    @PostMapping
    public ApiResponse<QuoteResponse> create(@Valid @RequestBody QuoteCreateRequest request) {
        QuoteResponse response = quoteService.createQuote(request);
        return ApiResponse.success(response);
    }

    /**
     * 更新报价单
     */
    @PutMapping("/{id}")
    public ApiResponse<QuoteResponse> update(@PathVariable Long id,
                                             @Valid @RequestBody QuoteUpdateRequest request) {
        QuoteResponse response = quoteService.updateQuote(id, request);
        return ApiResponse.success(response);
    }

    /**
     * 查询报价单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<QuoteResponse> getById(@PathVariable Long id) {
        QuoteResponse response = quoteService.getQuoteById(id);
        return ApiResponse.success(response);
    }

    /**
     * 提交审批
     */
    @PostMapping("/{id}/submit-approval")
    public ApiResponse<Void> submitApproval(@PathVariable Long id) {
        quoteService.submitForApproval(id);
        return ApiResponse.success();
    }

    /**
     * 审批通过
     */
    @PostMapping("/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable Long id,
                                     @Valid @RequestBody ApprovalRequest request) {
        quoteService.approveQuote(id, request);
        return ApiResponse.success();
    }

    /**
     * 审批驳回
     */
    @PostMapping("/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable Long id,
                                    @Valid @RequestBody ApprovalRequest request) {
        quoteService.rejectQuote(id, request);
        return ApiResponse.success();
    }

    /**
     * 报价单生成销售订单
     */
    @PostMapping("/{id}/generate-order")
    public ApiResponse<OrderResponse> generateOrder(@PathVariable Long id) {
        SalesOrder order = quoteService.generateOrderFromQuote(id);
        // 转换为 OrderResponse（需要实现 convert 方法）
        OrderResponse response = convertToOrderResponse(order);
        return ApiResponse.success(response);
    }

    // TODO: 分页查询接口

    private OrderResponse convertToOrderResponse(SalesOrder order) {
        // 临时实现，后续订单模块会有完整的转换工具
        return OrderResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}