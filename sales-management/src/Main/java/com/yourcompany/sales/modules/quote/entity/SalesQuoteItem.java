package com.yourcompany.sales.modules.quote.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 报价单明细实体
 */
@Getter
@Setter
@ToString(exclude = "quote")
@Entity
@Table(name = "sales_quote_item")
public class SalesQuoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private SalesQuote quote;               // 所属报价单

    @Column(name = "sku_id", nullable = false)
    private Long skuId;                     // SKU ID

    @Column(name = "sku_name_snapshot", length = 120)
    private String skuNameSnapshot;         // 下单时商品名称快照

    @Column(name = "qty", nullable = false)
    private Integer qty;                    // 数量

    @Column(name = "origin_unit_price", precision = 18, scale = 2)
    private BigDecimal originUnitPrice;     // 原单价

    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate = BigDecimal.ONE;  // 折扣率，默认1（无折扣）

    @Column(name = "deal_unit_price", precision = 18, scale = 2)
    private BigDecimal dealUnitPrice;       // 成交单价

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;             // 税率

    @Column(name = "line_amount", precision = 18, scale = 2)
    private BigDecimal lineAmount;          // 行金额（含税）

    @Column(name = "remark", length = 255)
    private String remark;                  // 备注

    /**
     * 计算行金额：成交单价 * 数量 * (1 + 税率)
     */
    public void calculateLineAmount() {
        if (originUnitPrice == null || qty == null) {
            this.lineAmount = BigDecimal.ZERO;
            return;
        }
        // 成交单价 = 原单价 * 折扣率
        BigDecimal rate = discountRate != null ? discountRate : BigDecimal.ONE;
        this.dealUnitPrice = originUnitPrice.multiply(rate);
        // 不含税小计
        BigDecimal subtotal = dealUnitPrice.multiply(BigDecimal.valueOf(qty));
        // 税额
        BigDecimal tax = BigDecimal.ZERO;
        if (taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0) {
            tax = subtotal.multiply(taxRate.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
        }
        // 含税总金额
        this.lineAmount = subtotal.add(tax);
    }
}