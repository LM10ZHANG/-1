package com.yourcompany.sales.modules.payment.service;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.modules.payment.dto.InvoiceRequest;
import com.yourcompany.sales.modules.payment.dto.InvoiceResponse;
import com.yourcompany.sales.modules.payment.dto.InvoiceQueryRequest;
import com.yourcompany.sales.modules.payment.dto.PaymentQueryRequest;
import com.yourcompany.sales.modules.payment.dto.PaymentRequest;
import com.yourcompany.sales.modules.payment.dto.PaymentResponse;
import com.yourcompany.sales.modules.payment.dto.ReceivableQueryRequest;
import com.yourcompany.sales.modules.payment.dto.ReceivableResponse;
import com.yourcompany.sales.modules.payment.dto.RefundQueryRequest;
import com.yourcompany.sales.modules.payment.dto.RefundRequest;
import com.yourcompany.sales.modules.payment.dto.RefundResponse;

public interface FinanceService {

    void createPayment(PaymentRequest req);

    PageResponse<PaymentResponse> pagePayments(PaymentQueryRequest req);

    PageResponse<ReceivableResponse> pageReceivables(ReceivableQueryRequest req);

    void createInvoice(InvoiceRequest req);

    PageResponse<InvoiceResponse> pageInvoices(InvoiceQueryRequest req);

    void createRefund(RefundRequest req);

    void finishRefund(Long refundId);

    void rejectRefund(Long refundId);

    PageResponse<RefundResponse> pageRefunds(RefundQueryRequest req);

}
