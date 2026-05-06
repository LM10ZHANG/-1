package com.yourcompany.sales.modules.dashboard.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.modules.customer.entity.Customer;
import com.yourcompany.sales.modules.customer.entity.CustomerFollowup;
import com.yourcompany.sales.modules.customer.repository.CustomerFollowupRepository;
import com.yourcompany.sales.modules.customer.repository.CustomerRepository;
import com.yourcompany.sales.modules.dashboard.dto.DashboardOverviewVO;
import com.yourcompany.sales.modules.dashboard.dto.DashboardQuery;
import com.yourcompany.sales.modules.dashboard.dto.DashboardWarningsVO;
import com.yourcompany.sales.modules.dashboard.dto.RankingItemVO;
import com.yourcompany.sales.modules.dashboard.dto.SalesTrendVO;
import com.yourcompany.sales.modules.dashboard.dto.WarningItemVO;
import com.yourcompany.sales.modules.dashboard.enums.DashboardScope;
import com.yourcompany.sales.modules.dashboard.enums.RankingType;
import com.yourcompany.sales.modules.dashboard.service.DashboardService;
import com.yourcompany.sales.modules.dashboard.support.DashboardRangeResolver;
import com.yourcompany.sales.modules.dashboard.support.TimeRange;
import com.yourcompany.sales.modules.order.entity.SalesOrder;
import com.yourcompany.sales.modules.order.repository.OrderRepository;
import com.yourcompany.sales.modules.payment.entity.PaymentRecord;
import com.yourcompany.sales.modules.payment.reposity.PaymentRecordRepository;
import com.yourcompany.sales.modules.product.entity.ProductSku;
import com.yourcompany.sales.modules.product.repository.ProductSkuRepository;
import com.yourcompany.sales.modules.quote.repository.QuoteRepository;
import com.yourcompany.sales.modules.stock.repository.InventoryStockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRangeResolver rangeResolver;
    private final QuoteRepository quoteRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final CustomerFollowupRepository customerFollowupRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final InventoryStockRepository inventoryStockRepository;
    private final ProductSkuRepository productSkuRepository;

    @Override
    public DashboardOverviewVO getOverview(DashboardQuery query) {
        TimeRange selectedRange = resolveRange(query);
        TimeRange monthRange = rangeResolver.resolve(DashboardScope.MONTH, null, null);
        TimeRange todayRange = rangeResolver.resolve(DashboardScope.TODAY, null, null);

        Long pendingApprovalQuotes = quoteRepository.countPendingApprovalQuotes();
        Long pendingOutboundOrders = orderRepository.countPendingOutboundOrders();
        Long pendingReceivableOrders = orderRepository.countPendingReceivableOrders();
        Long todayNewCustomers = customerRepository.countNewCustomers(todayRange.start(), todayRange.end());
        BigDecimal monthOrderAmount = orderRepository.sumOrderAmount(monthRange.start(), monthRange.end());
        BigDecimal monthPaymentAmount = paymentRecordRepository.sumValidPaymentAmount(
                monthRange.start(), monthRange.end());
        Long totalOrders = orderRepository.countOrders(selectedRange.start(), selectedRange.end());
        Long completedOrders = orderRepository.countCompletedOrders(selectedRange.start(), selectedRange.end());

        DashboardOverviewVO vo = new DashboardOverviewVO();
        vo.setPendingApprovalQuotes(nullSafeLong(pendingApprovalQuotes));
        vo.setPendingOutboundOrders(nullSafeLong(pendingOutboundOrders));
        vo.setPendingReceivableOrders(nullSafeLong(pendingReceivableOrders));
        vo.setTodayNewCustomers(nullSafeLong(todayNewCustomers));
        vo.setMonthOrderAmount(nullSafeDecimal(monthOrderAmount));
        vo.setMonthPaymentAmount(nullSafeDecimal(monthPaymentAmount));
        vo.setRoleView("BOSS");

        if (nullSafeLong(totalOrders) == 0L) {
            vo.setOrderCompletionRate(BigDecimal.ZERO);
        } else {
            vo.setOrderCompletionRate(
                    BigDecimal.valueOf(completedOrders)
                            .divide(BigDecimal.valueOf(totalOrders), 4, java.math.RoundingMode.HALF_UP));
        }
        return vo;
    }

    @Override
    public PageResponse<RankingItemVO> getRankings(DashboardQuery query, RankingType type, int page, int size) {
        TimeRange range = resolveRange(query);

        List<RankingItemVO> all = switch (type) {
            case SALES -> buildSalesRanking(range);
            case PRODUCT -> buildProductRanking(range);
            case CUSTOMER -> buildCustomerRanking(range);
        };

        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : size;
        int fromIndex = Math.min(safePage * safeSize, all.size());
        int toIndex = Math.min(fromIndex + safeSize, all.size());
        List<RankingItemVO> content = all.subList(fromIndex, toIndex);

        PageResponse<RankingItemVO> response = new PageResponse<>();
        response.setList(content);
        response.setTotal((long) all.size());
        response.setPageNum(safePage + 1);
        response.setPageSize(safeSize);
        response.setPages((int) Math.ceil((double) all.size() / safeSize));
        return response;
    }

    @Override
    public DashboardWarningsVO getWarnings(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : size;
        int offset = safePage * safeSize;

        DashboardWarningsVO vo = new DashboardWarningsVO();
        vo.setOverdueFollowCustomers(buildOverdueFollowWarnings(offset, safeSize));
        vo.setLowStockWarnings(buildLowStockWarnings(offset, safeSize));
        vo.setOverdueReceivables(buildOverdueReceivableWarnings(offset, safeSize));
        return vo;
    }

    @Override
    public List<SalesTrendVO> getSalesTrend(DashboardQuery query) {
        TimeRange range = resolveRange(query);

        Map<LocalDate, SalesTrendVO> trendMap = new LinkedHashMap<>();
        for (LocalDate date = range.start().toLocalDate(); !date.isAfter(range.end().toLocalDate()); date = date.plusDays(1)) {
            trendMap.put(date, new SalesTrendVO(date, BigDecimal.ZERO, BigDecimal.ZERO));
        }

        List<SalesOrder> orders = orderRepository.findByCreatedAtBetween(range.start(), range.end());
        for (SalesOrder order : orders) {
            if (order.getCreatedAt() == null) {
                continue;
            }
            SalesTrendVO vo = trendMap.get(order.getCreatedAt().toLocalDate());
            if (vo != null) {
                vo.setOrderAmount(vo.getOrderAmount().add(nullSafeDecimal(order.getTotalAmount())));
            }
        }

        List<PaymentRecord> payments = paymentRecordRepository.findByPayTimeBetweenAndStatus(
                range.start(), range.end(), "VALID");
        for (PaymentRecord payment : payments) {
            if (payment.getPayTime() == null) {
                continue;
            }
            SalesTrendVO vo = trendMap.get(payment.getPayTime().toLocalDate());
            if (vo != null) {
                vo.setPaymentAmount(vo.getPaymentAmount().add(nullSafeDecimal(payment.getPayAmount())));
            }
        }

        return new ArrayList<>(trendMap.values());
    }

    private List<RankingItemVO> buildSalesRanking(TimeRange range) {
        List<SalesOrder> orders = orderRepository.findByCreatedAtBetween(range.start(), range.end());
        Map<Long, RankingItemVO> map = new LinkedHashMap<>();

        for (SalesOrder order : orders) {
            Long userId = order.getOwnerUserId();
            RankingItemVO vo = map.computeIfAbsent(userId,
                    key -> new RankingItemVO(key, "sales-" + key, BigDecimal.ZERO, 0, 0));
            vo.setAmount(vo.getAmount().add(nullSafeDecimal(order.getTotalAmount())));
            vo.setCount(vo.getCount() + 1);
        }

        return sortAndRank(map);
    }

    private List<RankingItemVO> buildProductRanking(TimeRange range) {
        List<SalesOrder> orders = orderRepository.findByCreatedAtBetween(range.start(), range.end());
        Map<Long, RankingItemVO> map = new LinkedHashMap<>();

        for (SalesOrder order : orders) {
            if (order.getItems() == null) {
                continue;
            }

            order.getItems().forEach(item -> {
                Long skuId = item.getSkuId();
                ProductSku sku = skuId == null ? null : productSkuRepository.findById(skuId).orElse(null);
                String skuName = sku == null ? "SKU-" + skuId : sku.getSkuName();

                RankingItemVO vo = map.computeIfAbsent(skuId,
                        key -> new RankingItemVO(key, skuName, BigDecimal.ZERO, 0, 0));
                vo.setAmount(vo.getAmount().add(nullSafeDecimal(item.getLineAmount())));
                vo.setCount(vo.getCount() + safeQty(item.getQty()));
            });
        }

        return sortAndRank(map);
    }

    private List<RankingItemVO> buildCustomerRanking(TimeRange range) {
        List<SalesOrder> orders = orderRepository.findByCreatedAtBetween(range.start(), range.end());
        Map<Long, RankingItemVO> map = new LinkedHashMap<>();

        for (SalesOrder order : orders) {
            Long customerId = order.getCustomerId();
            Customer customer = customerId == null ? null : customerRepository.findById(customerId).orElse(null);
            String customerName = customer == null ? "customer-" + customerId : customer.getCustomerName();

            RankingItemVO vo = map.computeIfAbsent(customerId,
                    key -> new RankingItemVO(key, customerName, BigDecimal.ZERO, 0, 0));
            vo.setAmount(vo.getAmount().add(nullSafeDecimal(order.getTotalAmount())));
            vo.setCount(vo.getCount() + 1);
        }

        return sortAndRank(map);
    }

    private List<RankingItemVO> sortAndRank(Map<Long, RankingItemVO> map) {
        List<RankingItemVO> list = map.values().stream()
                .sorted(Comparator.comparing(RankingItemVO::getAmount, Comparator.reverseOrder())
                        .thenComparing(RankingItemVO::getCount, Comparator.reverseOrder()))
                .toList();

        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRankNo(i + 1);
        }
        return list;
    }

    private List<WarningItemVO> buildOverdueFollowWarnings(int offset, int size) {
        List<Customer> customers = customerRepository.findCustomersForWarning();
        List<WarningItemVO> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Customer customer : customers) {
            Optional<CustomerFollowup> latestOptional =
                    customerFollowupRepository.findTopByCustomerIdAndDeletedFlagOrderByCreatedAtDesc(
                            customer.getId(), 0);

            if (latestOptional.isEmpty()) {
                result.add(new WarningItemVO(
                        customer.getId(),
                        customer.getCustomerCode(),
                        customer.getCustomerName(),
                        "OVERDUE_FOLLOW",
                        "followup missing"));
                continue;
            }

            CustomerFollowup latest = latestOptional.get();
            boolean overdue = latest.getNextFollowTime() != null && latest.getNextFollowTime().isBefore(now);
            boolean stale = latest.getCreatedAt() != null && latest.getCreatedAt().isBefore(now.minusDays(7));

            if (overdue || stale) {
                result.add(new WarningItemVO(
                        customer.getId(),
                        customer.getCustomerCode(),
                        customer.getCustomerName(),
                        "OVERDUE_FOLLOW",
                        "followup overdue"));
            }
        }

        return paginateWarnings(result, offset, size);
    }

    private List<WarningItemVO> buildLowStockWarnings(int offset, int size) {
        return inventoryStockRepository.findLowStockWarnings().stream()
                .map(stock -> new WarningItemVO(
                        stock.getId(),
                        String.valueOf(stock.getSkuId()),
                        "SKU-" + stock.getSkuId(),
                        "LOW_STOCK",
                        "available=" + safeQty(stock.getAvailableQty()) + ", warn=" + safeQty(stock.getWarnQty())))
                .skip(offset)
                .limit(size)
                .toList();
    }

    private List<WarningItemVO> buildOverdueReceivableWarnings(int offset, int size) {
        LocalDateTime deadline = LocalDateTime.now().minusDays(7);

        return orderRepository.findOverdueReceivableOrders(deadline)
                .stream()
                .map(order -> new WarningItemVO(
                        order.getId(),
                        order.getOrderNo(),
                        "order-" + order.getOrderNo(),
                        "OVERDUE_RECEIVABLE",
                        "receivable overdue more than 7 days"))
                .skip(offset)
                .limit(size)
                .toList();
    }

    private List<WarningItemVO> paginateWarnings(List<WarningItemVO> warnings, int offset, int size) {
        return warnings.stream()
                .skip(offset)
                .limit(size)
                .toList();
    }

    private TimeRange resolveRange(DashboardQuery query) {
        return rangeResolver.resolve(query.getScope(), query.getStartDate(), query.getEndDate());
    }

    private Long nullSafeLong(Long value) {
        return value == null ? 0L : value;
    }

    private BigDecimal nullSafeDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Integer safeQty(Integer value) {
        return value == null ? 0 : value;
    }
}
