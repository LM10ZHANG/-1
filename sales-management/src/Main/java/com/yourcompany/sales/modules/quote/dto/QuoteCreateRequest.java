package com.yourcompany.sales.modules.quote.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 创建报价单请求 DTO
 */
@Data
public class QuoteCreateRequest {

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    private Long contactId;

    private LocalDate expireDate;

    private String paymentTerm;

    private String deliveryMethod;

    private Boolean taxIncludedFlag = true;

    private BigDecimal discountAmount = BigDecimal.ZERO;

    private String remark;

    @NotEmpty(message = "报价明细不能为空")
    @Valid
    private List<QuoteItemRequest> items;
}