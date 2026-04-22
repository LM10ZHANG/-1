package com.yourcompany.sales.modules.order.dto;

import com.yourcompany.sales.common.enums.OrderStatus;
import com.yourcompany.sales.common.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String orderNo;
    private Long quoteId;
    private Long customerId;
    private String customerName;
    private Long contactId;
    private String contactName;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private String shippingAddress;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private Long ownerUserId;
    private String ownerUserName;
    private String remark;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}