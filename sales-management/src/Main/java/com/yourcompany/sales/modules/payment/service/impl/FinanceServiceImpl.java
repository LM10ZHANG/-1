package com.yourcompany.sales.modules.payment.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.yourcompany.sales.modules.order.dto.OrderResponse;
import com.yourcompany.sales.modules.order.service.OrderService;
import com.yourcompany.sales.modules.payment.DTO.InvoiceRequest;
import com.yourcompany.sales.modules.payment.DTO.InvoiceResponse;
import com.yourcompany.sales.modules.payment.DTO.PaymentRequest;
import com.yourcompany.sales.modules.payment.DTO.ReceivableResponse;
import com.yourcompany.sales.modules.payment.DTO.RefundRequest;
import com.yourcompany.sales.modules.payment.entity.InvoiceRecord;
import com.yourcompany.sales.modules.payment.entity.PaymentRecord;
import com.yourcompany.sales.modules.payment.entity.RefundRecord;
import com.yourcompany.sales.modules.payment.reposity.InvoiceRecordRepository;
import com.yourcompany.sales.modules.payment.reposity.PaymentRecordRepository;
import com.yourcompany.sales.modules.payment.reposity.RefundRecordRepository;
import com.yourcompany.sales.modules.payment.service.FinanceService;

import jakarta.transaction.Transactional;
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
        OrderResponse order = orderService.getOrderById(req.getOrderId());
        BigDecimal totalAmount = safeAmount(order.getTotalAmount());
        BigDecimal netReceived = getNetReceivedAmount(req.getOrderId());
        BigDecimal payAmount = safeAmount(req.getPayAmount());

        if (netReceived.add(payAmount).compareTo(totalAmount) > 0) {
            throw new RuntimeException("收款金额超过应收金额");
        }

        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo("PAY" + System.currentTimeMillis());
        record.setOrderId(req.getOrderId());
        record.setCustomerId(req.getCustomerId());
        record.setPayAmount(req.getPayAmount());
        record.setPayMethod(req.getPayMethod());
        record.setVoucherUrl(req.getVoucherUrl());
        record.setOperatorUserId(req.getOperatorUserId());
        record.setRemark(req.getRemark());
        record.setStatus("VALID");
        record.setPayTime(LocalDateTime.now());
        record.setCreatedAt(LocalDateTime.now());

        paymentRepo.save(record);
        orderService.updatePaymentProgress(req.getOrderId(), netReceived.add(payAmount));
    }

    @Override
    public ReceivableResponse getReceivable(Long orderId) {
        OrderResponse order = orderService.getOrderById(orderId);
        BigDecimal totalAmount = safeAmount(order.getTotalAmount());
        BigDecimal receivedAmount = getNetReceivedAmount(orderId);

        ReceivableResponse response = new ReceivableResponse();
        response.setTotalAmount(totalAmount);
        response.setReceivedAmount(receivedAmount);
        response.setUnreceivedAmount(totalAmount.subtract(receivedAmount));

        LocalDate dueDate = order.getDeliveryDate() != null ? order.getDeliveryDate() : order.getOrderDate();
        if (dueDate != null && response.getUnreceivedAmount().compareTo(BigDecimal.ZERO) > 0) {
            long overdueDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
            response.setOverdueDays(Math.max(overdueDays, 0));
        } else {
            response.setOverdueDays(0L);
        }
        return response;
    }

    @Override
    public List<ReceivableResponse> getReceivables() {
        return orderService.getAllOrders().stream()
                .map(order -> getReceivable(order.getId()))
                .toList();
    }

    @Override
    @Transactional
    public void createInvoice(InvoiceRequest req) {
        BigDecimal paidAmount = getNetReceivedAmount(req.getOrderId());
        BigDecimal issuedAmount = safeAmount(invoiceRepo.sumIssuedAmountByOrderId(req.getOrderId()));
        BigDecimal invoiceAmount = safeAmount(req.getInvoiceAmount());

        if (issuedAmount.add(invoiceAmount).compareTo(paidAmount) > 0) {
            throw new RuntimeException("开票金额超过可开票金额");
        }

        InvoiceRecord invoice = new InvoiceRecord();
        invoice.setInvoiceNo("INV" + System.currentTimeMillis());
        invoice.setOrderId(req.getOrderId());
        invoice.setInvoiceTitle(req.getInvoiceTitle());
        invoice.setTaxNo(req.getTaxNo());
        invoice.setInvoiceAmount(req.getInvoiceAmount());
        invoice.setInvoiceStatus("ISSUED");
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setCreatedAt(LocalDateTime.now());

        invoiceRepo.save(invoice);
    }

    @Override
    public List<PaymentRecord> getPayments(Long orderId) {
        return orderId != null ? paymentRepo.findByOrderId(orderId) : paymentRepo.findAll();
    }

    @Override
    public List<InvoiceResponse> getInvoices(Long orderId) {
        List<InvoiceRecord> invoices = orderId != null ? invoiceRepo.findByOrderId(orderId) : invoiceRepo.findAll();
        return invoices.stream().map(invoice -> {
            InvoiceResponse response = new InvoiceResponse();
            response.setInvoiceNo(invoice.getInvoiceNo());
            response.setOrderId(invoice.getOrderId());
            response.setInvoiceAmount(invoice.getInvoiceAmount());
            response.setInvoiceStatus(invoice.getInvoiceStatus());
            response.setInvoiceDate(invoice.getInvoiceDate());
            return response;
        }).toList();
    }

    @Override
    @Transactional
    public void createRefund(RefundRequest req) {
        PaymentRecord payment = paymentRepo.findById(req.getPaymentId())
                .orElseThrow(() -> new RuntimeException("收款记录不存在"));

        if (!payment.getOrderId().equals(req.getOrderId())) {
            throw new RuntimeException("订单与收款记录不匹配");
        }

        BigDecimal refundedAmount = safeAmount(refundRepo.sumFinishedByPaymentId(req.getPaymentId()));
        BigDecimal refundAmount = safeAmount(req.getRefundAmount());
        if (refundedAmount.add(refundAmount).compareTo(safeAmount(payment.getPayAmount())) > 0) {
            throw new RuntimeException("退款金额超过原收款金额");
        }

        RefundRecord record = new RefundRecord();
        record.setRefundNo("REF" + System.currentTimeMillis());
        record.setOrderId(req.getOrderId());
        record.setPaymentId(req.getPaymentId());
        record.setRefundAmount(req.getRefundAmount());
        record.setRefundReason(req.getRefundReason());
        record.setStatus("WAIT");
        record.setCreatedAt(LocalDateTime.now());

        refundRepo.save(record);
    }

    @Override
    @Transactional
    public void finishRefund(Long refundId) {
        RefundRecord record = refundRepo.findById(refundId)
                .orElseThrow(() -> new RuntimeException("退款记录不存在"));

        if (!"WAIT".equals(record.getStatus())) {
            throw new RuntimeException("退款状态异常");
        }

        record.setStatus("FINISHED");
        record.setRefundTime(LocalDateTime.now());
        refundRepo.save(record);

        orderService.updatePaymentProgress(record.getOrderId(), getNetReceivedAmount(record.getOrderId()));
    }

    @Override
    @Transactional
    public void rejectRefund(Long refundId) {
        RefundRecord record = refundRepo.findById(refundId)
                .orElseThrow(() -> new RuntimeException("退款记录不存在"));
        record.setStatus("REJECTED");
        refundRepo.save(record);
    }

    @Override
    public List<RefundRecord> getRefunds(Long orderId) {
        return orderId != null ? refundRepo.findByOrderId(orderId) : refundRepo.findAll();
    }

    private BigDecimal getNetReceivedAmount(Long orderId) {
        BigDecimal paidAmount = safeAmount(paymentRepo.sumValidAmountByOrderId(orderId));
        BigDecimal refundedAmount = safeAmount(refundRepo.sumFinishedAmountByOrderId(orderId));
        return paidAmount.subtract(refundedAmount).max(BigDecimal.ZERO);
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
