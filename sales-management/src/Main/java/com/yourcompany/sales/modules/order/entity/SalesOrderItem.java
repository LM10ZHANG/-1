package com.yourcompany.sales.modules.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 销售订单明细实体
 */
@Getter
@Setter
@ToString(exclude = "order")
@Entity
@Table(name = "sales_order_item")
public class SalesOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private SalesOrder order;

    @Column(name = "sku_id", nullable = false)
    private Long skuId;

    @Column(name = "sku_name_snapshot", length = 120)
    private String skuNameSnapshot;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Column(name = "locked_qty")
    private Integer lockedQty = 0;

    @Column(name = "outbound_qty")
    private Integer outboundQty = 0;

    @Column(name = "unit_price", precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate = BigDecimal.ONE;

    @Column(name = "line_amount", precision = 18, scale = 2)
    private BigDecimal lineAmount;

    @Column(name = "remark", length = 255)
    private String remark;

    /**
     * 计算行金额：单价 * 数量 * (1 + 税率) * 折扣率
     */
    public void calculateLineAmount() {
        if (unitPrice == null || qty == null) {
            this.lineAmount = BigDecimal.ZERO;
            return;
        }
        BigDecimal rate = discountRate != null ? discountRate : BigDecimal.ONE;
        BigDecimal priceAfterDiscount = unitPrice.multiply(rate);
        BigDecimal subtotal = priceAfterDiscount.multiply(BigDecimal.valueOf(qty));
        BigDecimal tax = BigDecimal.ZERO;
        if (taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0) {
            tax = subtotal.multiply(taxRate.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
        }
        this.lineAmount = subtotal.add(tax);
    }
}