package com.yourcompany.sales.modules.quote.entity;

import com.yourcompany.sales.common.dto.BaseEntity;
import com.yourcompany.sales.common.enums.ApprovalStatus;
import com.yourcompany.sales.common.enums.QuoteStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 报价单主表实体
 */
@Getter
@Setter
@ToString(callSuper = true, exclude = "items")
@Entity
@Table(name = "sales_quote")
public class SalesQuote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quote_no", unique = true, nullable = false, length = 50)
    private String quoteNo;                 // 报价单号

    @Column(name = "customer_id", nullable = false)
    private Long customerId;                // 客户ID

    @Column(name = "contact_id")
    private Long contactId;                 // 联系人ID

    @Column(name = "quote_date")
    private LocalDate quoteDate;            // 报价日期

    @Column(name = "expire_date")
    private LocalDate expireDate;           // 有效期至

    @Column(name = "payment_term", length = 50)
    private String paymentTerm;             // 付款条件

    @Column(name = "delivery_method", length = 50)
    private String deliveryMethod;          // 交付方式

    @Column(name = "tax_included_flag")
    private Boolean taxIncludedFlag;        // 是否含税

    @Column(name = "discount_amount", precision = 18, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;  // 整单优惠金额

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;     // 报价总额

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private QuoteStatus status;              // 报价单状态

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", length = 20)
    private ApprovalStatus approvalStatus;   // 审批状态

    @Column(name = "owner_user_id")
    private Long ownerUserId;                // 销售负责人

    @Column(name = "remark", length = 255)
    private String remark;                   // 备注

    // 一对多关联报价明细，级联操作
    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SalesQuoteItem> items = new ArrayList<>();

    /**
     * 计算总金额（基于明细）
     */
    public void calculateTotal() {
        if (items == null || items.isEmpty()) {
            this.totalAmount = BigDecimal.ZERO;
            return;
        }
        this.totalAmount = items.stream()
                .map(SalesQuoteItem::getLineAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 减去整单优惠
        if (discountAmount != null) {
            this.totalAmount = this.totalAmount.subtract(discountAmount);
        }
    }

    /**
     * 添加报价明细，维护双向关系
     */
    public void addItem(SalesQuoteItem item) {
        items.add(item);
        item.setQuote(this);
    }

    /**
     * 移除报价明细
     */
    public void removeItem(SalesQuoteItem item) {
        items.remove(item);
        item.setQuote(null);
    }
}