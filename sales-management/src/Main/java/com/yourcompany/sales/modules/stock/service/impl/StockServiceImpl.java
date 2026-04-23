package com.yourcompany.sales.modules.stock.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yourcompany.sales.modules.dashboard.entity.NotificationMessage;
import com.yourcompany.sales.modules.dashboard.reposity.NotificationRepository;
import com.yourcompany.sales.modules.order.dto.OrderItemResponse;
import com.yourcompany.sales.modules.order.dto.OrderResponse;
import com.yourcompany.sales.modules.order.service.OrderService;
import com.yourcompany.sales.modules.stock.DTO.OutboundRequest;
import com.yourcompany.sales.modules.stock.DTO.ReturnRequest;
import com.yourcompany.sales.modules.stock.DTO.StockItemDTO;
import com.yourcompany.sales.modules.stock.DTO.StockLockRequest;
import com.yourcompany.sales.modules.stock.entity.InventoryStock;
import com.yourcompany.sales.modules.stock.entity.StockLockRecord;
import com.yourcompany.sales.modules.stock.entity.StockOutRecord;
import com.yourcompany.sales.modules.stock.repository.InventoryStockRepository;
import com.yourcompany.sales.modules.stock.repository.StockLockRecordRepository;
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
    private final NotificationRepository msgRepo;
    private final OrderService orderService;

    @Override
    public List<InventoryStock> list(Long skuId) {
        return skuId != null ? stockRepo.findBySkuId(skuId) : stockRepo.findAll();
    }

    @Transactional
    @Override
    public void lockStock(StockLockRequest req) {
        OrderResponse order = orderService.getOrderById(req.getOrderId());

        for (StockItemDTO item : req.getItems()) {
            OrderItemResponse orderItem = getOrderItem(order, item.getSkuId());
            int lockQty = safeQty(item.getQuantity());
            int currentLocked = safeQty(orderItem.getLockedQty());
            int currentOutbound = safeQty(orderItem.getOutboundQty());

            if (lockQty <= 0) {
                throw new RuntimeException("锁定数量必须大于0");
            }
            if (currentLocked + currentOutbound + lockQty > safeQty(orderItem.getQty())) {
                throw new RuntimeException("锁库记录与实际出库数量不一致");
            }

            InventoryStock stock = getStock(req.getWarehouseId(), item.getSkuId());
            if (safeQty(stock.getAvailableQty()) < lockQty) {
                throw new RuntimeException("库存不足");
            }

            stock.setAvailableQty(safeQty(stock.getAvailableQty()) - lockQty);
            stock.setLockedQty(safeQty(stock.getLockedQty()) + lockQty);
            stock.setUpdatedAt(LocalDateTime.now());
            stockRepo.save(stock);
            checkAndCreateWarn(stock);

            StockLockRecord record = new StockLockRecord();
            record.setOrderId(req.getOrderId());
            record.setWarehouseId(req.getWarehouseId());
            record.setSkuId(item.getSkuId());
            record.setQuantity(lockQty);
            lockRepo.save(record);

            orderService.updateOrderItemStockProgress(req.getOrderId(), item.getSkuId(), lockQty, 0);
        }
    }

    @Transactional
    @Override
    public void releaseStock(Long orderId) {
        List<StockLockRecord> records = lockRepo.findByOrderId(orderId);

        for (StockLockRecord record : records) {
            InventoryStock stock = getStock(record.getWarehouseId(), record.getSkuId());
            int releaseQty = safeQty(record.getQuantity());

            stock.setLockedQty(safeQty(stock.getLockedQty()) - releaseQty);
            stock.setAvailableQty(safeQty(stock.getAvailableQty()) + releaseQty);
            stock.setUpdatedAt(LocalDateTime.now());
            stockRepo.save(stock);

            orderService.updateOrderItemStockProgress(orderId, record.getSkuId(), -releaseQty, 0);
        }

        lockRepo.deleteByOrderId(orderId);
    }

    @Transactional
    @Override
    public void outbound(OutboundRequest req) {
        OrderResponse order = orderService.getOrderById(req.getOrderId());

        for (StockItemDTO item : req.getItems()) {
            OrderItemResponse orderItem = getOrderItem(order, item.getSkuId());
            int outboundQty = safeQty(item.getQuantity());
            int remainingQty = safeQty(orderItem.getQty()) - safeQty(orderItem.getOutboundQty());

            if (outboundQty <= 0) {
                throw new RuntimeException("出库数量必须大于0");
            }
            if (outboundQty > remainingQty) {
                throw new RuntimeException("出库数量超过剩余可出库数量");
            }

            InventoryStock stock = getStock(req.getWarehouseId(), item.getSkuId());
            if (safeQty(stock.getLockedQty()) < outboundQty) {
                throw new RuntimeException("锁定库存不足");
            }

            stock.setLockedQty(safeQty(stock.getLockedQty()) - outboundQty);
            stock.setTotalQty(safeQty(stock.getTotalQty()) - outboundQty);
            stock.setUpdatedAt(LocalDateTime.now());
            stockRepo.save(stock);
            checkAndCreateWarn(stock);

            StockOutRecord outRecord = new StockOutRecord();
            outRecord.setOutboundNo("OUT" + System.currentTimeMillis() + item.getSkuId());
            outRecord.setOrderId(req.getOrderId());
            outRecord.setWarehouseId(req.getWarehouseId());
            outRecord.setOperatorUserId(SecurityUtils.getCurrentUserId());
            outRecord.setRemark("SKU=" + item.getSkuId() + ", qty=" + outboundQty);
            outRecord.setStatus("DONE");
            outRecord.setOutboundTime(LocalDateTime.now());
            outRecord.setCreatedAt(LocalDateTime.now());
            outRepo.save(outRecord);

            consumeLockRecords(req.getOrderId(), item.getSkuId(), outboundQty);
            orderService.updateOrderItemStockProgress(req.getOrderId(), item.getSkuId(), -outboundQty, outboundQty);
        }
    }

    @Transactional
    @Override
    public void returnInbound(ReturnRequest req) {
        for (StockItemDTO item : req.getItems()) {
            InventoryStock stock = getStock(req.getWarehouseId(), item.getSkuId());
            int returnQty = safeQty(item.getQuantity());
            if (returnQty <= 0) {
                throw new RuntimeException("入库数量必须大于0");
            }

            stock.setTotalQty(safeQty(stock.getTotalQty()) + returnQty);
            stock.setAvailableQty(safeQty(stock.getAvailableQty()) + returnQty);
            stock.setUpdatedAt(LocalDateTime.now());
            stockRepo.save(stock);
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

    private OrderItemResponse getOrderItem(OrderResponse order, Long skuId) {
        return order.getItems().stream()
                .filter(item -> item.getSkuId().equals(skuId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("订单中不存在该SKU"));
    }

    private InventoryStock getStock(Long warehouseId, Long skuId) {
        return stockRepo.findByWarehouseIdAndSkuId(warehouseId, skuId)
                .orElseThrow(() -> new RuntimeException("库存不存在"));
    }

    private void consumeLockRecords(Long orderId, Long skuId, int quantity) {
        int remaining = quantity;
        for (StockLockRecord record : lockRepo.findByOrderIdAndSkuId(orderId, skuId)) {
            if (remaining <= 0) {
                break;
            }
            int recordQty = safeQty(record.getQuantity());
            if (recordQty <= remaining) {
                remaining -= recordQty;
                lockRepo.delete(record);
            } else {
                record.setQuantity(recordQty - remaining);
                lockRepo.save(record);
                remaining = 0;
            }
        }
        if (remaining > 0) {
            throw new RuntimeException("锁库记录与实际库存不一致");
        }
    }

    private int safeQty(Integer quantity) {
        return quantity == null ? 0 : quantity;
    }
}
