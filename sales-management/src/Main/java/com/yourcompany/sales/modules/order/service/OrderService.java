package com.yourcompany.sales.modules.order.service;

import com.yourcompany.sales.common.enums.OrderStatus;
import com.yourcompany.sales.common.enums.PaymentStatus;
import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.order.dto.*;
import com.yourcompany.sales.modules.order.entity.SalesOrder;
import com.yourcompany.sales.modules.order.entity.SalesOrderItem;
import com.yourcompany.sales.modules.order.repository.OrderItemRepository;
import com.yourcompany.sales.modules.order.repository.OrderRepository;
import com.yourcompany.sales.utils.BeanCopyUtils;
import com.yourcompany.sales.utils.CodeGenerator;
import com.yourcompany.sales.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CodeGenerator codeGenerator;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        SalesOrder order = new SalesOrder();
        order.setOrderNo(codeGenerator.generateOrderNo());
        order.setQuoteId(request.getQuoteId());
        order.setCustomerId(request.getCustomerId());
        order.setContactId(request.getContactId());
        order.setOrderDate(LocalDate.now());
        order.setDeliveryDate(request.getDeliveryDate());
        order.setShippingAddress(request.getShippingAddress());
        order.setDiscountAmount(request.getDiscountAmount());
        order.setRemark(request.getRemark());
        order.setOrderStatus(OrderStatus.DRAFT);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setOwnerUserId(SecurityUtils.getCurrentUserId());

        List<SalesOrderItem> items = new ArrayList<>();
        for (OrderItemRequest itemReq : request.getItems()) {
            SalesOrderItem item = new SalesOrderItem();
            item.setSkuId(itemReq.getSkuId());
            item.setSkuNameSnapshot("商品快照");
            item.setQty(itemReq.getQty());
            item.setUnitPrice(new BigDecimal("100.00"));
            item.setTaxRate(new BigDecimal("13.00"));
            item.setDiscountRate(itemReq.getDiscountRate());
            item.calculateLineAmount();
            item.setLockedQty(0);
            item.setOutboundQty(0);
            order.addItem(item);
        }
        order.calculateTotal();

        SalesOrder saved = orderRepository.save(order);
        log.info("创建订单成功, orderNo: {}", saved.getOrderNo());
        return convertToResponse(saved);
    }

    @Transactional
    public OrderResponse updateOrder(Long id, OrderUpdateRequest request) {
        SalesOrder order = orderRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("订单", id));

        if (order.getOrderStatus() != OrderStatus.DRAFT && order.getOrderStatus() != OrderStatus.PENDING_APPROVAL) {
            throw BusinessException.invalidStatus(order.getOrderStatus().getDescription(), "可编辑状态");
        }

        order.setCustomerId(request.getCustomerId());
        order.setContactId(request.getContactId());
        order.setDeliveryDate(request.getDeliveryDate());
        order.setShippingAddress(request.getShippingAddress());
        order.setDiscountAmount(request.getDiscountAmount());
        order.setRemark(request.getRemark());

        orderItemRepository.deleteByOrderId(id);
        order.getItems().clear();
        for (OrderItemRequest itemReq : request.getItems()) {
            SalesOrderItem item = new SalesOrderItem();
            item.setSkuId(itemReq.getSkuId());
            item.setSkuNameSnapshot("商品快照");
            item.setQty(itemReq.getQty());
            item.setUnitPrice(new BigDecimal("100.00"));
            item.setTaxRate(new BigDecimal("13.00"));
            item.setDiscountRate(itemReq.getDiscountRate());
            item.calculateLineAmount();
            order.addItem(item);
        }
        order.calculateTotal();

        SalesOrder updated = orderRepository.save(order);
        return convertToResponse(updated);
    }

    @Transactional
    public void cancelOrder(Long id) {
        SalesOrder order = orderRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("订单", id));

        if (!order.getOrderStatus().canCancel()) {
            throw BusinessException.invalidStatus(order.getOrderStatus().getDescription(), "可取消状态");
        }

        if (order.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("订单已有收款，请先处理退款");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("订单已取消, orderId: {}", id);
    }

    public OrderResponse getOrderById(Long id) {
        SalesOrder order = orderRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("订单", id));
        return convertToResponse(order);
    }

    public OrderTimelineVo getOrderTimeline(Long id) {
        SalesOrder order = orderRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("订单", id));

        List<OrderEventVo> events = new ArrayList<>();
        events.add(OrderEventVo.builder()
                .eventType("CREATE")
                .description("订单创建")
                .operator("系统")
                .eventTime(order.getCreatedAt())
                .build());

        return OrderTimelineVo.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .events(events)
                .build();
    }

    private OrderResponse convertToResponse(SalesOrder order) {
        OrderResponse response = BeanCopyUtils.copyBean(order, OrderResponse.class);
        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (SalesOrderItem item : order.getItems()) {
            OrderItemResponse itemResp = BeanCopyUtils.copyBean(item, OrderItemResponse.class);
            itemResponses.add(itemResp);
        }
        response.setItems(itemResponses);
        return response;
    }
}