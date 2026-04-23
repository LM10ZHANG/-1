package com.yourcompany.sales.modules.payment.service;

import java.util.List;

import com.yourcompany.sales.modules.payment.DTO.InvoiceRequest;
import com.yourcompany.sales.modules.payment.DTO.InvoiceResponse;
import com.yourcompany.sales.modules.payment.DTO.PaymentRequest;
import com.yourcompany.sales.modules.payment.DTO.ReceivableResponse;
import com.yourcompany.sales.modules.payment.DTO.RefundRequest;
import com.yourcompany.sales.modules.payment.entity.PaymentRecord;
import com.yourcompany.sales.modules.payment.entity.RefundRecord;

public interface FinanceService {

        void createPayment(PaymentRequest req);

        ReceivableResponse getReceivable(Long orderId);

        void createInvoice(InvoiceRequest req);

        List<PaymentRecord> getPayments(Long orderId);

        List<ReceivableResponse> getReceivables();

        List<InvoiceResponse> getInvoices(Long orderId);
        
        void createRefund(RefundRequest req);

        void finishRefund(Long refundId);

        void rejectRefund(Long refundId);

        List<RefundRecord> getRefunds(Long orderId);

}
