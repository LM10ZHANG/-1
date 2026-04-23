package com.yourcompany.sales.modules.dashboard.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.yourcompany.sales.common.enums.OrderStatus;
import com.yourcompany.sales.common.enums.PaymentStatus;
import com.yourcompany.sales.modules.dashboard.DTO.DashboardWarningsVO;
import com.yourcompany.sales.modules.dashboard.DTO.OverviewVO;
import com.yourcompany.sales.modules.dashboard.DTO.ProductRankingVO;
import com.yourcompany.sales.modules.dashboard.DTO.SalesTrendVO;
import com.yourcompany.sales.modules.dashboard.entity.NotificationMessage;
import com.yourcompany.sales.modules.dashboard.reposity.NotificationRepository;
import com.yourcompany.sales.modules.dashboard.service.DashboardService;
import com.yourcompany.sales.modules.order.dto.OrderItemResponse;
import com.yourcompany.sales.modules.order.dto.OrderResponse;
import com.yourcompany.sales.modules.order.service.OrderService;
import com.yourcompany.sales.modules.payment.entity.PaymentRecord;
import com.yourcompany.sales.modules.payment.reposity.PaymentRecordRepository;
import com.yourcompany.sales.modules.stock.entity.InventoryStock;
import com.yourcompany.sales.modules.stock.repository.InventoryStockRepository;
import com.yourcompany.sales.utils.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderService orderService;
    private final PaymentRecordRepository paymentRepo;
    private final InventoryStockRepository stockRepo;
    private final NotificationRepository notificationRepo;

    @Override
    public OverviewVO getOverview(LocalDateTime start, LocalDateTime end) {
        List<OrderResponse> orders = orderService.getOrdersByTimeRange(start, end);
        BigDecimal totalOrderAmount = orders.stream()
                .map(OrderResponse::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalOrders = orders.size();
        long completed = orders.stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.COMPLETED)
                .count();
        long pendingOutbound = orders.stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.WAIT_OUTBOUND
                        || order.getOrderStatus() == OrderStatus.WAIT_STOCK)
                .count();
        long pendingPayment = orders.stream()
                .filter(order -> order.getPaymentStatus() != PaymentStatus.PAID)
                .count();

        BigDecimal totalPayment = paymentRepo.sumByTimeRange(start, end);

        OverviewVO vo = new OverviewVO();
        vo.setTotalOrders(totalOrders);
        vo.setTotalOrderAmount(totalOrderAmount);
        vo.setTotalPayment(totalPayment == null ? BigDecimal.ZERO : totalPayment);
        vo.setCompletionRate(totalOrders == 0 ? 0D : (double) completed / totalOrders);
        vo.setPendingOutbound(pendingOutbound);
        vo.setPendingPayment(pendingPayment);
        return vo;
    }

    @Override
    public List<ProductRankingVO> getProductRanking(LocalDateTime start, LocalDateTime end) {
        List<OrderResponse> orders = orderService.getOrdersByTimeRange(start, end);
        Map<Long, ProductRankingVO> rankingMap = new HashMap<>();

        for (OrderResponse order : orders) {
            for (OrderItemResponse item : order.getItems()) {
                ProductRankingVO ranking = rankingMap.computeIfAbsent(
                        item.getSkuId(),
                        key -> new ProductRankingVO(key, 0, BigDecimal.ZERO));
                ranking.setTotalQty(ranking.getTotalQty() + safeQty(item.getQty()));
                BigDecimal lineAmount = item.getLineAmount() == null ? BigDecimal.ZERO : item.getLineAmount();
                ranking.setTotalAmount(ranking.getTotalAmount().add(lineAmount));
            }
        }

        return rankingMap.values().stream()
                .sorted((a, b) -> {
                    int amountCompare = b.getTotalAmount().compareTo(a.getTotalAmount());
                    return amountCompare != 0 ? amountCompare : b.getTotalQty() - a.getTotalQty();
                })
                .limit(10)
                .toList();
    }

    @Override
    public List<InventoryStock> getLowStockWarnings() {
        return stockRepo.findAll().stream()
                .filter(stock -> safeQty(stock.getAvailableQty()) <= safeQty(stock.getWarnQty()))
                .toList();
    }

    @Override
    public List<OrderResponse> getOverduePayments() {
        LocalDateTime deadline = LocalDateTime.now().minusDays(7);
        return orderService.getAllOrders().stream()
                .filter(order -> order.getPaymentStatus() != PaymentStatus.PAID)
                .filter(order -> order.getCreatedAt() != null && order.getCreatedAt().isBefore(deadline))
                .toList();
    }

    @Override
    public DashboardWarningsVO getWarnings() {
        DashboardWarningsVO warnings = new DashboardWarningsVO();
        warnings.setLowStockWarnings(getLowStockWarnings());
        warnings.setOverduePayments(getOverduePayments());

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<NotificationMessage> unreadMessages = currentUserId != null
                ? notificationRepo.findByReceiverUserIdAndReadFlag(currentUserId, 0)
                : notificationRepo.findByReadFlag(0);
        warnings.setUnreadMessages(unreadMessages);
        return warnings;
    }

    @Override
    public List<SalesTrendVO> getSalesTrend(LocalDate start, LocalDate end) {
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(23, 59, 59);

        Map<LocalDate, BigDecimal> orderAmountMap = new TreeMap<>();
        for (OrderResponse order : orderService.getOrdersByTimeRange(startTime, endTime)) {
            if (order.getCreatedAt() == null) {
                continue;
            }
            orderAmountMap.merge(order.getCreatedAt().toLocalDate(),
                    order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount(),
                    BigDecimal::add);
        }

        Map<LocalDate, BigDecimal> paymentAmountMap = new TreeMap<>();
        for (PaymentRecord payment : paymentRepo.findByPayTimeBetweenAndStatus(startTime, endTime, "VALID")) {
            if (payment.getPayTime() == null) {
                continue;
            }
            paymentAmountMap.merge(payment.getPayTime().toLocalDate(),
                    payment.getPayAmount() == null ? BigDecimal.ZERO : payment.getPayAmount(),
                    BigDecimal::add);
        }

        Map<LocalDate, SalesTrendVO> trendMap = new TreeMap<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            trendMap.put(date, new SalesTrendVO(date, BigDecimal.ZERO, BigDecimal.ZERO));
        }
        orderAmountMap.forEach((date, amount) -> trendMap.get(date).setOrderAmount(amount));
        paymentAmountMap.forEach((date, amount) -> trendMap.get(date).setPaymentAmount(amount));

        return trendMap.values().stream().toList();
    }

    private int safeQty(Integer quantity) {
        return quantity == null ? 0 : quantity;
    }
}
