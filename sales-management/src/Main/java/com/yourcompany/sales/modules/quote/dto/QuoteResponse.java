package com.yourcompany.sales.modules.quote.dto;

import com.yourcompany.sales.common.enums.ApprovalStatus;
import com.yourcompany.sales.common.enums.QuoteStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报价单响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteResponse {

    private Long id;

    private String quoteNo;

    private Long customerId;

    private String customerName;        // 客户名称（需从客户服务获取）

    private Long contactId;

    private String contactName;         // 联系人姓名

    private LocalDate quoteDate;

    private LocalDate expireDate;

    private String paymentTerm;

    private String deliveryMethod;

    private Boolean taxIncludedFlag;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private QuoteStatus status;

    private ApprovalStatus approvalStatus;

    private Long ownerUserId;

    private String ownerUserName;       // 负责人姓名

    private String remark;

    private List<QuoteItemResponse> items;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}