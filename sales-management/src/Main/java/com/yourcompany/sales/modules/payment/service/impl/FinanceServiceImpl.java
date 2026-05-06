package com.yourcompany.sales.modules.payment.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.order.dto.OrderResponse;
import com.yourcompany.sales.modules.order.service.OrderService;
import com.yourcompany.sales.modules.payment.dto.InvoiceQueryRequest;
import com.yourcompany.sales.modules.payment.dto.InvoiceRequest;
import com.yourcompany.sales.modules.payment.dto.InvoiceResponse;
import com.yourcompany.sales.modules.payment.dto.PaymentQueryRequest;
import com.yourcompany.sales.modules.payment.dto.PaymentRequest;
import com.yourcompany.sales.modules.payment.dto.PaymentResponse;
import com.yourcompany.sales.modules.payment.dto.ReceivableQueryRequest;
import com.yourcompany.sales.modules.payment.dto.ReceivableResponse;
import com.yourcompany.sales.modules.payment.dto.RefundQueryRequest;
import com.yourcompany.sales.modules.payment.dto.RefundRequest;
import com.yourcompany.sales.modules.payment.dto.RefundResponse;
import com.yourcompany.sales.modules.payment.entity.InvoiceRecord;
import com.yourcompany.sales.modules.payment.entity.PaymentRecord;
import com.yourcompany.sales.modules.payment.entity.RefundRecord;
import com.yourcompany.sales.modules.payment.reposity.InvoiceRecordRepository;
import com.yourcompany.sales.modules.payment.reposity.PaymentRecordRepository;
import com.yourcompany.sales.modules.payment.reposity.RefundRecordRepository;
import com.yourcompany.sales.modules.payment.service.FinanceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private final PaymentRecordRepository paymentRepo;
    private final InvoiceRecordRepository invoiceRepo;
    private final RefundRecordRepository refundRepo;
    private final OrderService orderService;

    @Override
    @Transactional
    public void createPayment(PaymentRequest req) {
        if (req.getOrderId() == null || req.getCustomerId() == null) {
            throw new BusinessException("订单和客户不能为空");
        }
        BigDecimal payAmount = safeAmount(req.getPayAmount());
        if (payAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("收款金额必须大于0");
        }

        OrderResponse order = orderService.getOrderById(req.getOrderId());
        if (order.getCustomerId() != null && !order.getCustomerId().equals(req.getCustomerId())) {
            throw new BusinessException("客户与订单不匹配");
        }

        BigDecimal totalAmount = safeAmount(order.getTotalAmount());
        BigDecimal netReceived = getNetReceivedAmount(req.getOrderId());
        if (netReceived.add(payAmount).compareTo(totalAmount) > 0) {
            throw new BusinessException("累计收款不能超过订单应收金额");
        }

        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo("PAY" + System.currentTimeMillis());
        record.setOrderId(req.getOrderId());
        record.setCustomerId(req.getCustomerId());
        record.setPayAmount(payAmount);
        record.setPayMethod(req.getPayMethod());
        record.setVoucherUrl(req.getVoucherUrl());
        record.setOperatorUserId(req.getOperatorUserId());
        record.setRemark(req.getRemark());
        record.setStatus("VALID");
        record.setPayTime(req.getPayTime() == null ? LocalDateTime.now() : req.getPayTime());
        record.setCreatedAt(LocalDateTime.now());

        paymentRepo.save(record);
        orderService.updatePaymentProgress(req.getOrderId(), netReceived.add(payAmount));
    }

    @Override
    public PageResponse<PaymentResponse> pagePayments(PaymentQueryRequest req) {
        List<PaymentResponse> all = paymentRepo.findAll().stream()
                .filter(p -> req.getOrderId() == null || req.getOrderId().equals(p.getOrderId()))
                .filter(p -> req.getCustomerId() == null || req.getCustomerId().equals(p.getCustomerId()))
                .filter(p -> isBlank(req.getPaymentNo()) || contains(p.getPaymentNo(), req.getPaymentNo()))
                .filter(p -> isBlank(req.getPayMethod()) || req.getPayMethod().equals(p.getPayMethod()))
                .filter(p -> isBlank(req.getStatus()) || req.getStatus().equals(p.getStatus()))
                .filter(p -> req.getStartTime() == null || !safeTime(p.getPayTime()).isBefore(req.getStartTime()))
                .filter(p -> req.getEndTime() == null || !safeTime(p.getPayTime()).isAfter(req.getEndTime()))
                .sorted(Comparator.comparing(PaymentRecord::getPayTime, Comparator.nullsLast(LocalDateTime::compareTo)).reversed())
                .map(this::toPaymentResponse)
                .toList();
        return toPage(all, req.getPageNum(), req.getPageSize());
    }

    @Override
    public PageResponse<ReceivableResponse> pageReceivables(ReceivableQueryRequest req) {
        List<ReceivableResponse> all = orderService.getAllOrders().stream()
                .filter(order -> req.getOrderId() == null || req.getOrderId().equals(order.getId()))
                .filter(order -> req.getCustomerId() == null || req.getCustomerId().equals(order.getCustomerId()))
                .filter(order -> req.getStartDate() == null || !safeDate(order.getOrderDate()).isBefore(req.getStartDate()))
                .filter(order -> req.getEndDate() == null || !safeDate(order.getOrderDate()).isAfter(req.getEndDate()))
                .map(this::toReceivableResponse)
                .filter(r -> isBlank(req.getPaymentStatus()) || req.getPaymentStatus().equals(r.getPaymentStatus()))
                .filter(r -> req.getOverdueOnly() == null || !req.getOverdueOnly() || safeLong(r.getOverdueDays()) > 0L)
                .toList();
        return toPage(all, req.getPageNum(), req.getPageSize());
    }

    @Override
    @Transactional
    public void createInvoice(InvoiceRequest req) {
        if (req.getOrderId() == null) {
            throw new BusinessException("订单不能为空");
        }
        BigDecimal invoiceAmount = safeAmount(req.getInvoiceAmount());
        if (invoiceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("开票金额必须大于0");
        }

        OrderResponse order = orderService.getOrderById(req.getOrderId());
        BigDecimal issuedAmount = safeAmount(invoiceRepo.sumIssuedAmountByOrderId(req.getOrderId()));
        if (issuedAmount.add(invoiceAmount).compareTo(safeAmount(order.getTotalAmount())) > 0) {
            throw new BusinessException("累计开票金额不能超过订单总额");
        }

        InvoiceRecord invoice = new InvoiceRecord();
        invoice.setInvoiceNo("INV" + System.currentTimeMillis());
        invoice.setOrderId(req.getOrderId());
        invoice.setInvoiceTitle(req.getInvoiceTitle());
        invoice.setTaxNo(req.getTaxNo());
        invoice.setInvoiceAmount(invoiceAmount);
        invoice.setInvoiceStatus(isBlank(req.getInvoiceStatus()) ? "ISSUED" : req.getInvoiceStatus());
        invoice.setInvoiceDate(req.getInvoiceDate() == null ? LocalDate.now() : req.getInvoiceDate());
        invoice.setCreatedAt(LocalDateTime.now());

        invoiceRepo.save(invoice);
    }

    @Override
    public PageResponse<InvoiceResponse> pageInvoices(InvoiceQueryRequest req) {
        List<InvoiceResponse> all = invoiceRepo.findAll().stream()
                .filter(i -> req.getOrderId() == null || req.getOrderId().equals(i.getOrderId()))
                .filter(i -> isBlank(req.getInvoiceNo()) || contains(i.getInvoiceNo(), req.getInvoiceNo()))
                .filter(i -> isBlank(req.getInvoiceStatus()) || req.getInvoiceStatus().equals(i.getInvoiceStatus()))
                .filter(i -> req.getStartDate() == null || !safeDate(i.getInvoiceDate()).isBefore(req.getStartDate()))
                .filter(i -> req.getEndDate() == null || !safeDate(i.getInvoiceDate()).isAfter(req.getEndDate()))
                .sorted(Comparator.comparing(InvoiceRecord::getInvoiceDate, Comparator.nullsLast(LocalDate::compareTo)).reversed())
                .map(this::toInvoiceResponse)
                .toList();
        return toPage(all, req.getPageNum(), req.getPageSize());
    }

    @Override
    @Transactional
    public void createRefund(RefundRequest req) {
        BigDecimal refundAmount = safeAmount(req.getRefundAmount());
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("退款金额必须大于0");
        }

        PaymentRecord payment = paymentRepo.findById(req.getPaymentId())
                .orElseThrow(() -> BusinessException.notFound("收款记录", req.getPaymentId()));
        if (!"VALID".equals(payment.getStatus())) {
            throw new BusinessException("只有有效收款才能退款");
        }
        if (!payment.getOrderId().equals(req.getOrderId())) {
            throw new BusinessException("订单与原收款不匹配");
        }

        BigDecimal refundedAmount = safeAmount(refundRepo.sumFinishedByPaymentId(req.getPaymentId()));
        if (refundedAmount.add(refundAmount).compareTo(safeAmount(payment.getPayAmount())) > 0) {
            throw new BusinessException("退款金额不能超过原收款可退余额");
        }

        RefundRecord record = new RefundRecord();
        record.setRefundNo("REF" + System.currentTimeMillis());
        record.setOrderId(req.getOrderId());
        record.setPaymentId(req.getPaymentId());
        record.setRefundAmount(refundAmount);
        record.setRefundReason(req.getRefundReason());
        record.setRefundTime(req.getRefundTime());
        record.setStatus("WAIT");
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());

        refundRepo.save(record);
    }

    @Override
    @Transactional
    public void finishRefund(Long refundId) {
        RefundRecord record = refundRepo.findById(refundId)
                .orElseThrow(() -> BusinessException.notFound("退款记录", refundId));
        if (!"WAIT".equals(record.getStatus())) {
            throw new BusinessException("当前退款状态不允许完成");
        }

        record.setStatus("FINISHED");
        if (record.getRefundTime() == null) {
            record.setRefundTime(LocalDateTime.now());
        }
        record.setUpdatedAt(LocalDateTime.now());
        refundRepo.save(record);

        orderService.updatePaymentProgress(record.getOrderId(), getNetReceivedAmount(record.getOrderId()));
    }

    @Override
    @Transactional
    public void rejectRefund(Long refundId) {
        RefundRecord record = refundRepo.findById(refundId)
                .orElseThrow(() -> BusinessException.notFound("退款记录", refundId));
        if (!"WAIT".equals(record.getStatus())) {
            throw new BusinessException("当前退款状态不允许驳回");
        }

        record.setStatus("REJECTED");
        record.setUpdatedAt(LocalDateTime.now());
        refundRepo.save(record);
    }

    @Override
    public PageResponse<RefundResponse> pageRefunds(RefundQueryRequest req) {
        List<RefundResponse> all = refundRepo.findAll().stream()
                .filter(r -> req.getOrderId() == null || req.getOrderId().equals(r.getOrderId()))
                .filter(r -> req.getPaymentId() == null || req.getPaymentId().equals(r.getPaymentId()))
                .filter(r -> isBlank(req.getStatus()) || req.getStatus().equals(r.getStatus()))
                .filter(r -> req.getStartTime() == null || !safeTime(r.getCreatedAt()).isBefore(req.getStartTime()))
                .filter(r -> req.getEndTime() == null || !safeTime(r.getCreatedAt()).isAfter(req.getEndTime()))
                .sorted(Comparator.comparing(RefundRecord::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo)).reversed())
                .map(this::toRefundResponse)
                .toList();
        return toPage(all, req.getPageNum(), req.getPageSize());
    }

    private PaymentResponse toPaymentResponse(PaymentRecord payment) {
        OrderResponse order = safeGetOrder(payment.getOrderId());

        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setPaymentNo(payment.getPaymentNo());
        response.setOrderId(payment.getOrderId());
        response.setOrderNo(order == null ? null : order.getOrderNo());
        response.setCustomerId(payment.getCustomerId());
        response.setCustomerName(order == null ? null : order.getCustomerName());
        response.setPayAmount(payment.getPayAmount());
        response.setPayMethod(payment.getPayMethod());
        response.setPayTime(payment.getPayTime());
        response.setVoucherUrl(payment.getVoucherUrl());
        response.setOperatorUserId(payment.getOperatorUserId());
        response.setStatus(payment.getStatus());
        response.setRemark(payment.getRemark());
        return response;
    }

    private ReceivableResponse toReceivableResponse(OrderResponse order) {
        BigDecimal totalAmount = safeAmount(order.getTotalAmount());
        BigDecimal receivedAmount = getNetReceivedAmount(order.getId());
        BigDecimal unreceivedAmount = totalAmount.subtract(receivedAmount).max(BigDecimal.ZERO);

        ReceivableResponse response = new ReceivableResponse();
        response.setOrderId(order.getId());
        response.setOrderNo(order.getOrderNo());
        response.setCustomerId(order.getCustomerId());
        response.setCustomerName(order.getCustomerName());
        response.setOrderDate(order.getOrderDate());
        response.setDeliveryDate(order.getDeliveryDate());
        response.setTotalAmount(totalAmount);
        response.setReceivedAmount(receivedAmount);
        response.setUnreceivedAmount(unreceivedAmount);
        response.setPaymentStatus(order.getPaymentStatus() == null ? null : order.getPaymentStatus().name());

        LocalDate dueDate = order.getDeliveryDate() != null ? order.getDeliveryDate() : order.getOrderDate();
        long overdueDays = 0L;
        if (dueDate != null && unreceivedAmount.compareTo(BigDecimal.ZERO) > 0) {
            overdueDays = Math.max(0L, ChronoUnit.DAYS.between(dueDate, LocalDate.now()));
        }
        response.setOverdueDays(overdueDays);
        response.setRiskLevel(overdueDays >= 30 ? "HIGH" : overdueDays >= 7 ? "MEDIUM" : "LOW");
        return response;
    }

    private InvoiceResponse toInvoiceResponse(InvoiceRecord invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setInvoiceNo(invoice.getInvoiceNo());
        response.setOrderId(invoice.getOrderId());
        response.setInvoiceTitle(invoice.getInvoiceTitle());
        response.setTaxNo(invoice.getTaxNo());
        response.setInvoiceAmount(invoice.getInvoiceAmount());
        response.setInvoiceStatus(invoice.getInvoiceStatus());
        response.setInvoiceDate(invoice.getInvoiceDate());
        return response;
    }

    private RefundResponse toRefundResponse(RefundRecord record) {
        RefundResponse response = new RefundResponse();
        response.setId(record.getId());
        response.setRefundNo(record.getRefundNo());
        response.setOrderId(record.getOrderId());
        response.setPaymentId(record.getPaymentId());
        response.setRefundAmount(record.getRefundAmount());
        response.setRefundReason(record.getRefundReason());
        response.setRefundTime(record.getRefundTime());
        response.setStatus(record.getStatus());
        response.setCreatedAt(record.getCreatedAt());
        return response;
    }

    private OrderResponse safeGetOrder(Long orderId) {
        try {
            return orderService.getOrderById(orderId);
        } catch (Exception ex) {
            return null;
        }
    }

    private BigDecimal getNetReceivedAmount(Long orderId) {
        BigDecimal paidAmount = safeAmount(paymentRepo.sumValidAmountByOrderId(orderId));
        BigDecimal refundedAmount = safeAmount(refundRepo.sumFinishedAmountByOrderId(orderId));
        return paidAmount.subtract(refundedAmount).max(BigDecimal.ZERO);
    }

    private <T> PageResponse<T> toPage(List<T> all, Integer pageNum, Integer pageSize) {
        int pn = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int ps = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int from = Math.min((pn - 1) * ps, all.size());
        int to = Math.min(from + ps, all.size());
        return PageResponse.of(all.subList(from, to), (long) all.size(), pn, ps);
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private LocalDateTime safeTime(LocalDateTime time) {
        return time == null ? LocalDateTime.MIN : time;
    }

    private LocalDate safeDate(LocalDate date) {
        return date == null ? LocalDate.MIN : date;
    }

    private long safeLong(Long value) {
        return value == null ? 0L : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean contains(String source, String target) {
        return source != null && target != null && source.contains(target);
    }
}
