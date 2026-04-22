package com.yourcompany.sales.modules.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderCreateRequest {

    private Long quoteId;   // 如果从报价单生成

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    private Long contactId;

    private LocalDate deliveryDate;

    private String shippingAddress;

    private BigDecimal discountAmount = BigDecimal.ZERO;

    private String remark;

    @NotEmpty(message = "订单明细不能为空")
    @Valid
    private List<OrderItemRequest> items;
}