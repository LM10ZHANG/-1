package com.yourcompany.sales.modules.stock.service.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.dashboard.entity.NotificationMessage;
import com.yourcompany.sales.modules.dashboard.reposity.NotificationRepository;
import com.yourcompany.sales.modules.order.dto.OrderItemResponse;
import com.yourcompany.sales.modules.order.dto.OrderResponse;
import com.yourcompany.sales.modules.order.service.OrderService;
import com.yourcompany.sales.modules.product.entity.ProductSku;
import com.yourcompany.sales.modules.product.repository.ProductSkuRepository;
import com.yourcompany.sales.modules.stock.dto.OutboundDetailResponse;
import com.yourcompany.sales.modules.stock.dto.OutboundItemResponse;
import com.yourcompany.sales.modules.stock.dto.OutboundQueryRequest;
import com.yourcompany.sales.modules.stock.dto.OutboundRequest;
import com.yourcompany.sales.modules.stock.dto.OutboundResponse;
import com.yourcompany.sales.modules.stock.dto.ReturnRequest;
import com.yourcompany.sales.modules.stock.dto.StockChangeResponse;
import com.yourcompany.sales.modules.stock.dto.StockDetailResponse;
import com.yourcompany.sales.modules.stock.dto.StockItemDTO;
import com.yourcompany.sales.modules.stock.dto.StockLockRequest;
import com.yourcompany.sales.modules.stock.dto.StockQueryRequest;
import com.yourcompany.sales.modules.stock.dto.StockReleaseRequest;
import com.yourcompany.sales.modules.stock.dto.StockResponse;
import com.yourcompany.sales.modules.stock.entity.InventoryStock;
import com.yourcompany.sales.modules.stock.entity.StockChangeRecord;
import com.yourcompany.sales.modules.stock.entity.StockLockRecord;
import com.yourcompany.sales.modules.stock.entity.StockOutItemRecord;
import com.yourcompany.sales.modules.stock.entity.StockOutRecord;
import com.yourcompany.sales.modules.stock.repository.InventoryStockRepository;
import com.yourcompany.sales.modules.stock.repository.StockChangeRecordRepository;
import com.yourcompany.sales.modules.stock.repository.StockLockRecordRepository;
import com.yourcompany.sales.modules.stock.repository.StockOutItemRecordRepository;
import com.yourcompany.sales.modules.stock.repository.StockOutRecordRepository;
import com.yourcompany.sales.modules.stock.service.StockService;
import com.yourcompany.sales.utils.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final InventoryStockRepository stockRepo;
    private final StockLockRecordRepository lockRepo;
    private final StockOutRecordRepository outRepo;
    private final StockOutItemRecordRepository outItemRepo;
    private final StockChangeRecordRepository changeRepo;
    private final NotificationRepository msgRepo;
    private final OrderService orderService;
    private final ProductSkuRepository productSkuRepository;

    @Override
    public PageResponse<StockResponse> pageStocks(StockQueryRequest req) {
        List<StockResponse> all = stockRepo.findAll().stream()
                .map(this::toStockResponse)
                .filter(s -> req.getWarehouseId() == null || req.getWarehouseId().equals(s.getWarehouseId()))
                .filter(s -> req.getSkuId() == null || req.getSkuId().equals(s.getSkuId()))
                .filter(s -> isBlank(req.getSkuCode()) || contains(s.getSkuCode(), req.getSkuCode()))
                .filter(s -> isBlank(req.getSkuName()) || contains(s.getSkuName(), req.getSkuName()))
                .filter(s -> req.getLowStockOnly() == null || !req.getLowStockOnly() || Boolean.TRUE.equals(s.getLowStock()))
                .sorted(Comparator.comparing(StockResponse::getUpdatedAt, Comparator.nullsLast(LocalDateTime::compareTo)).reversed())
                .toList();
        return toPage(all, req.getPageNum(), req.getPageSize());
    }

    @Override
    public StockDetailResponse getStockDetail(Long stockId) {
        InventoryStock stock = stockRepo.findById(stockId)
                .orElseThrow(() -> BusinessException.notFound("库存记录", stockId));

        StockDetailResponse response = new StockDetailResponse();
        response.setStock(toStockResponse(stock));
        response.setChangeRecords(changeRepo.findTop20ByStockIdOrderByCreatedAtDesc(stockId).stream()
                .map(this::toStockChangeResponse)
                .toList());
        return response;
    }

    @Transactional
    @Override
    public void lockStock(StockLockRequest req) {
        OrderResponse order = orderService.getOrderById(req.getOrderId());

        for (StockItemDTO item : req.getItems()) {
            OrderItemResponse orderItem = getOrderItem(order, item);
            int lockQty = safeQty(item.getQuantity());
            int currentLocked = safeQty(orderItem.getLockedQty());
            int currentOutbound = safeQty(orderItem.getOutboundQty());

            if (lockQty <= 0) {
                throw new BusinessException("锁定数量必须大于0");
            }
            if (currentLocked + currentOutbound + lockQty > safeQty(orderItem.getQty())) {
                throw new BusinessException("锁定数量与订单出库进度不匹配");
            }

            InventoryStock stock = getStock(req.getWarehouseId(), item.getSkuId());
            if (safeQty(stock.getAvailableQty()) < lockQty) {
                throw new BusinessException("可用库存不足");
            }

            int beforeTotal = safeQty(stock.getTotalQty());
            int beforeAvailable = safeQty(stock.getAvailableQty());
            int beforeLocked = safeQty(stock.getLockedQty());

            stock.setAvailableQty(beforeAvailable - lockQty);
            stock.setLockedQty(beforeLocked + lockQty);
            stock.setUpdatedAt(LocalDateTime.now());
            stockRepo.save(stock);
            checkAndCreateWarn(stock);

            StockLockRecord record = new StockLockRecord();
            record.setOrderId(req.getOrderId());
            record.setOrderItemId(orderItem.getId());
            record.setWarehouseId(req.getWarehouseId());
            record.setSkuId(item.getSkuId());
            record.setQuantity(lockQty);
            record.setRemark(req.getRemark());
            record.setStatus("LOCKED");
            record.setCreatedAt(LocalDateTime.now());
            record.setUpdatedAt(LocalDateTime.now());
            lockRepo.save(record);

            orderService.updateOrderItemStockProgress(req.getOrderId(), item.getSkuId(), lockQty, 0);
            saveChange(stock, "LOCK", lockQty, beforeTotal, beforeAvailable, beforeLocked, "ORDER", req.getOrderId(), req.getRemark());
        }
    }

    @Transactional
    @Override
    public void releaseStock(StockReleaseRequest req) {
        for (StockItemDTO item : req.getItems()) {
            InventoryStock stock = getStock(req.getWarehouseId(), item.getSkuId());
            int releaseQty = safeQty(item.getQuantity());
            if (releaseQty <= 0) {
                throw new BusinessException("释放数量必须大于0");
            }

            List<StockLockRecord> records = lockRepo.findByOrderIdAndSkuId(req.getOrderId(), item.getSkuId());
            int totalLocked = records.stream().mapToInt(r -> safeQty(r.getQuantity())).sum();
            if (releaseQty > totalLocked) {
                throw new BusinessException("释放数量超过已锁定库存");
            }

            int beforeTotal = safeQty(stock.getTotalQty());
            int beforeAvailable = safeQty(stock.getAvailableQty());
            int beforeLocked = safeQty(stock.getLockedQty());

            stock.setLockedQty(beforeLocked - releaseQty);
            stock.setAvailableQty(beforeAvailable + releaseQty);
            stock.setUpdatedAt(LocalDateTime.now());
            stockRepo.save(stock);

            consumeLockRecords(req.getOrderId(), item.getSkuId(), releaseQty, true);
            orderService.updateOrderItemStockProgress(req.getOrderId(), item.getSkuId(), -releaseQty, 0);
            saveChange(stock, "RELEASE", releaseQty, beforeTotal, beforeAvailable, beforeLocked, "ORDER", req.getOrderId(), req.getReason());
        }
    }

    @Transactional
    @Override
    public void outbound(OutboundRequest req) {
        OrderResponse order = orderService.getOrderById(req.getOrderId());
        if (order.getOrderStatus() == null || !order.getOrderStatus().canOutbound()) {
            throw BusinessException.invalidStatus(String.valueOf(order.getOrderStatus()), "WAIT_OUTBOUND");
        }

        StockOutRecord outRecord = new StockOutRecord();
        outRecord.setOutboundNo("OUT" + System.currentTimeMillis());
        outRecord.setOrderId(req.getOrderId());
        outRecord.setWarehouseId(req.getWarehouseId());
        outRecord.setOperatorUserId(SecurityUtils.getCurrentUserId());
        outRecord.setRemark(req.getRemark());
        outRecord.setStatus("FINISHED");
        outRecord.setOutboundTime(req.getOutboundTime() == null ? LocalDateTime.now() : req.getOutboundTime());
        outRecord.setCreatedAt(LocalDateTime.now());
        outRepo.save(outRecord);

        for (StockItemDTO item : req.getItems()) {
            OrderItemResponse orderItem = getOrderItem(order, item);
            int outboundQty = safeQty(item.getQuantity());

            if (outboundQty <= 0) {
                throw new BusinessException("出库数量必须大于0");
            }
            if (outboundQty > safeQty(orderItem.getLockedQty())) {
                throw new BusinessException("出库数量不能超过已锁定数量");
            }

            InventoryStock stock = getStock(req.getWarehouseId(), item.getSkuId());
            if (safeQty(stock.getLockedQty()) < outboundQty) {
                throw new BusinessException("锁定库存不足");
            }

            int beforeTotal = safeQty(stock.getTotalQty());
            int beforeAvailable = safeQty(stock.getAvailableQty());
            int beforeLocked = safeQty(stock.getLockedQty());

            stock.setLockedQty(beforeLocked - outboundQty);
            stock.setTotalQty(beforeTotal - outboundQty);
            stock.setUpdatedAt(LocalDateTime.now());
            stockRepo.save(stock);
            checkAndCreateWarn(stock);

            StockOutItemRecord itemRecord = new StockOutItemRecord();
            itemRecord.setStockOutRecordId(outRecord.getId());
            itemRecord.setOrderItemId(orderItem.getId());
            itemRecord.setSkuId(item.getSkuId());
            itemRecord.setOutboundQty(outboundQty);
            outItemRepo.save(itemRecord);

            consumeLockRecords(req.getOrderId(), item.getSkuId(), outboundQty, false);
            orderService.updateOrderItemStockProgress(req.getOrderId(), item.getSkuId(), -outboundQty, outboundQty);
            saveChange(stock, "OUTBOUND", outboundQty, beforeTotal, beforeAvailable, beforeLocked, "OUTBOUND", outRecord.getId(), req.getRemark());
        }
    }

    @Override
    public PageResponse<OutboundResponse> pageOutboundOrders(OutboundQueryRequest req) {
        List<OutboundResponse> all = outRepo.findAll().stream()
                .filter(o -> req.getOrderId() == null || req.getOrderId().equals(o.getOrderId()))
                .filter(o -> req.getWarehouseId() == null || req.getWarehouseId().equals(o.getWarehouseId()))
                .filter(o -> isBlank(req.getOutboundNo()) || contains(o.getOutboundNo(), req.getOutboundNo()))
                .filter(o -> isBlank(req.getStatus()) || req.getStatus().equals(o.getStatus()))
                .filter(o -> req.getStartTime() == null || !safeTime(o.getOutboundTime()).isBefore(req.getStartTime()))
                .filter(o -> req.getEndTime() == null || !safeTime(o.getOutboundTime()).isAfter(req.getEndTime()))
                .sorted(Comparator.comparing(StockOutRecord::getOutboundTime, Comparator.nullsLast(LocalDateTime::compareTo)).reversed())
                .map(this::toOutboundResponse)
                .toList();
        return toPage(all, req.getPageNum(), req.getPageSize());
    }

    @Override
    public OutboundDetailResponse getOutboundDetail(Long id) {
        StockOutRecord record = outRepo.findById(id)
                .orElseThrow(() -> BusinessException.notFound("出库单", id));

        OutboundDetailResponse response = new OutboundDetailResponse();
        response.setHeader(toOutboundResponse(record));
        response.setItems(outItemRepo.findByStockOutRecordId(id).stream()
                .map(this::toOutboundItemResponse)
                .toList());
        return response;
    }

    @Transactional
    @Override
    public void returnInbound(ReturnRequest req) {
        for (StockItemDTO item : req.getItems()) {
            InventoryStock stock = getStock(req.getWarehouseId(), item.getSkuId());
            int returnQty = safeQty(item.getQuantity());
            if (returnQty <= 0) {
                throw new BusinessException("入库数量必须大于0");
            }

            int beforeTotal = safeQty(stock.getTotalQty());
            int beforeAvailable = safeQty(stock.getAvailableQty());
            int beforeLocked = safeQty(stock.getLockedQty());

            stock.setTotalQty(beforeTotal + returnQty);
            stock.setAvailableQty(beforeAvailable + returnQty);
            stock.setUpdatedAt(LocalDateTime.now());
            stockRepo.save(stock);

            saveChange(stock, "RETURN_INBOUND", returnQty, beforeTotal, beforeAvailable, beforeLocked, "ORDER", req.getOrderId(), req.getReason());
        }
    }

    @Override
    public void checkAndCreateWarn(InventoryStock stock) {
        if (safeQty(stock.getAvailableQty()) > safeQty(stock.getWarnQty())) {
            return;
        }

        boolean exists = msgRepo.existsByBizTypeAndBizIdAndReadFlag("STOCK", stock.getSkuId(), 0);
        if (exists) {
            return;
        }

        NotificationMessage msg = new NotificationMessage();
        msg.setReceiverUserId(SecurityUtils.getCurrentUserId());
        msg.setMsgType("STOCK_WARN");
        msg.setTitle("库存预警");
        msg.setContent("SKU=" + stock.getSkuId() + "可用库存" + stock.getAvailableQty());
        msg.setBizType("STOCK");
        msg.setBizId(stock.getSkuId());
        msg.setReadFlag(0);
        msg.setSendTime(LocalDateTime.now());
        msgRepo.save(msg);
    }

    private StockResponse toStockResponse(InventoryStock stock) {
        ProductSku sku = productSkuRepository.findById(stock.getSkuId()).orElse(null);

        StockResponse response = new StockResponse();
        response.setId(stock.getId());
        response.setWarehouseId(stock.getWarehouseId());
        response.setWarehouseName("仓库-" + stock.getWarehouseId());
        response.setSkuId(stock.getSkuId());
        response.setSkuCode(sku == null ? null : sku.getSkuCode());
        response.setSkuName(sku == null ? null : sku.getSkuName());
        response.setTotalQty(stock.getTotalQty());
        response.setAvailableQty(stock.getAvailableQty());
        response.setLockedQty(stock.getLockedQty());
        response.setWarnQty(stock.getWarnQty());
        response.setLowStock(safeQty(stock.getAvailableQty()) <= safeQty(stock.getWarnQty()));
        response.setUpdatedAt(stock.getUpdatedAt());
        return response;
    }

    private StockChangeResponse toStockChangeResponse(StockChangeRecord record) {
        StockChangeResponse response = new StockChangeResponse();
        response.setId(record.getId());
        response.setChangeType(record.getChangeType());
        response.setBeforeTotalQty(record.getBeforeTotalQty());
        response.setBeforeAvailableQty(record.getBeforeAvailableQty());
        response.setBeforeLockedQty(record.getBeforeLockedQty());
        response.setChangeQty(record.getChangeQty());
        response.setAfterTotalQty(record.getAfterTotalQty());
        response.setAfterAvailableQty(record.getAfterAvailableQty());
        response.setAfterLockedQty(record.getAfterLockedQty());
        response.setBizType(record.getBizType());
        response.setBizId(record.getBizId());
        response.setRemark(record.getRemark());
        response.setOperatorUserId(record.getOperatorUserId());
        response.setCreatedAt(record.getCreatedAt());
        return response;
    }

    private OutboundResponse toOutboundResponse(StockOutRecord record) {
        OutboundResponse response = new OutboundResponse();
        response.setId(record.getId());
        response.setOutboundNo(record.getOutboundNo());
        response.setOrderId(record.getOrderId());
        response.setWarehouseId(record.getWarehouseId());
        response.setStatus(record.getStatus());
        response.setRemark(record.getRemark());
        response.setOutboundTime(record.getOutboundTime());
        return response;
    }

    private OutboundItemResponse toOutboundItemResponse(StockOutItemRecord record) {
        OutboundItemResponse response = new OutboundItemResponse();
        response.setId(record.getId());
        response.setOrderItemId(record.getOrderItemId());
        response.setSkuId(record.getSkuId());
        response.setOutboundQty(record.getOutboundQty());
        return response;
    }

    private void saveChange(InventoryStock stock, String changeType, int changeQty,
                            int beforeTotalQty, int beforeAvailableQty, int beforeLockedQty,
                            String bizType, Long bizId, String remark) {
        StockChangeRecord record = new StockChangeRecord();
        record.setStockId(stock.getId());
        record.setWarehouseId(stock.getWarehouseId());
        record.setSkuId(stock.getSkuId());
        record.setChangeType(changeType);
        record.setBeforeTotalQty(beforeTotalQty);
        record.setBeforeAvailableQty(beforeAvailableQty);
        record.setBeforeLockedQty(beforeLockedQty);
        record.setChangeQty(changeQty);
        record.setAfterTotalQty(stock.getTotalQty());
        record.setAfterAvailableQty(stock.getAvailableQty());
        record.setAfterLockedQty(stock.getLockedQty());
        record.setBizType(bizType);
        record.setBizId(bizId);
        record.setRemark(remark);
        record.setOperatorUserId(SecurityUtils.getCurrentUserId());
        record.setCreatedAt(LocalDateTime.now());
        changeRepo.save(record);
    }

    private OrderItemResponse getOrderItem(OrderResponse order, StockItemDTO item) {
        if (item.getOrderItemId() != null) {
            return order.getItems().stream()
                    .filter(orderItem -> item.getOrderItemId().equals(orderItem.getId()))
                    .findFirst()
                    .orElseThrow(() -> BusinessException.notFound("订单明细", item.getOrderItemId()));
        }
        return order.getItems().stream()
                .filter(orderItem -> item.getSkuId().equals(orderItem.getSkuId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("订单中不存在该SKU"));
    }

    private InventoryStock getStock(Long warehouseId, Long skuId) {
        return stockRepo.findByWarehouseIdAndSkuId(warehouseId, skuId)
                .orElseThrow(() -> new BusinessException("库存不存在"));
    }

    private void consumeLockRecords(Long orderId, Long skuId, int quantity, boolean releaseOnly) {
        int remaining = quantity;
        for (StockLockRecord record : lockRepo.findByOrderIdAndSkuId(orderId, skuId)) {
            if (remaining <= 0) {
                break;
            }
            int currentQty = safeQty(record.getQuantity());
            if (currentQty <= remaining) {
                remaining -= currentQty;
                if (releaseOnly) {
                    record.setQuantity(0);
                    record.setStatus("RELEASED");
                    record.setUpdatedAt(LocalDateTime.now());
                    lockRepo.save(record);
                } else {
                    lockRepo.delete(record);
                }
            } else {
                record.setQuantity(currentQty - remaining);
                record.setUpdatedAt(LocalDateTime.now());
                lockRepo.save(record);
                remaining = 0;
            }
        }
        if (remaining > 0) {
            throw new BusinessException("锁库记录与实际库存不一致");
        }
    }

    private <T> PageResponse<T> toPage(List<T> all, Integer pageNum, Integer pageSize) {
        int pn = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int ps = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int from = Math.min((pn - 1) * ps, all.size());
        int to = Math.min(from + ps, all.size());
        return PageResponse.of(all.subList(from, to), (long) all.size(), pn, ps);
    }

    private int safeQty(Integer quantity) {
        return quantity == null ? 0 : quantity;
    }

    private LocalDateTime safeTime(LocalDateTime time) {
        return time == null ? LocalDateTime.MIN : time;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean contains(String source, String target) {
        return source != null && target != null && source.contains(target);
    }
}
