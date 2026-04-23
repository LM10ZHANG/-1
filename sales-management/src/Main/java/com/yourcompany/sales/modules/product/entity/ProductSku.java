package com.yourcompany.sales.modules.product.entity;

import com.yourcompany.sales.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 商品 SKU 实体（对应文档 7.9 product_sku）
 */
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "product_sku")
public class ProductSku extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spu_id", nullable = false)
    private Long spuId;

    @Column(name = "sku_code", unique = true, nullable = false, length = 50)
    private String skuCode;

    @Column(name = "sku_name", nullable = false, length = 120)
    private String skuName;

    @Column(name = "spec_json", columnDefinition = "TEXT")
    private String specJson;

    @Column(name = "barcode", length = 50)
    private String barcode;

    @Column(name = "sale_price", precision = 18, scale = 2)
    private BigDecimal salePrice = BigDecimal.ZERO;

    @Column(name = "cost_price", precision = 18, scale = 2)
    private BigDecimal costPrice = BigDecimal.ZERO;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name = "stock_warn_qty")
    private Integer stockWarnQty = 0;

    @Column(name = "status", nullable = false)
    private Integer status = 1;
}
