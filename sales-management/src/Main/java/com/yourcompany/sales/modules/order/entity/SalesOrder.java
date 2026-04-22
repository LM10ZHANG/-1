package com.yourcompany.sales.modules.order.entity;

import com.yourcompany.sales.common.dto.BaseEntity;
import com.yourcompany.sales.common.enums.ApprovalStatus;
import com.yourcompany.sales.common.enums.OrderStatus;
import com.yourcompany.sales.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 销售订单主表实体
 */
@Getter
@Setter
@ToString(callSuper = true, exclude = "items")
@Entity
@Table(name = "sales_order")
public class SalesOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", unique = true, nullable = false, length = 50)
    private String orderNo;                 // 订单编号

    @Column(name = "quote_id")
    private Long quoteId;                   // 来源报价单ID

    @Column(name = "customer_id", nullable = false)
    private Long customerId;                // 客户ID

    @Column(name = "contact_id")
    private Long contactId;                 // 联系人ID

    @Column(name = "order_date")
    private LocalDate orderDate;            // 下单日期

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;         // 交付日期

    @Column(name = "shipping_address", length = 255)
    private String shippingAddress;         // 收货地址

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 20)
    private OrderStatus orderStatus;        // 订单状态

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20)
    private PaymentStatus paymentStatus;    // 支付状态

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", length = 20)
    private ApprovalStatus approvalStatus;  // 审批状态（如需审批）

    @Column(name = "discount_amount", precision = 18, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "paid_amount", precision = 18, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "owner_user_id")
    private Long ownerUserId;                // 销售负责人

    @Column(name = "remark", length = 255)
    private String remark;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SalesOrderItem> items = new ArrayList<>();

    /**
     * 计算订单总额（基于明细和折扣）
     */
    public void calculateTotal() {
        if (items == null || items.isEmpty()) {
            this.totalAmount = BigDecimal.ZERO;
            return;
        }
        BigDecimal sum = items.stream()
                .map(SalesOrderItem::getLineAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalAmount = sum.subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
    }

    public void addItem(SalesOrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(SalesOrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }
}