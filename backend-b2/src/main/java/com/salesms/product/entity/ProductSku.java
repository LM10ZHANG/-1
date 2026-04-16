package com.salesms.product.entity;

import com.salesms.common.AuditFields;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_sku")
public class ProductSku extends AuditFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spu_id", nullable = false)
    private Long spuId;

    @Column(name = "sku_code", length = 50, nullable = false, unique = true)
    private String skuCode;

    @Column(name = "sku_name", length = 120, nullable = false)
    private String skuName;

    @Column(name = "spec_json", columnDefinition = "TEXT")
    private String specJson;

    @Column(name = "barcode", length = 50)
    private String barcode;

    @Column(name = "sale_price", precision = 18, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "cost_price", precision = 18, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "stock_warn_qty")
    private Integer stockWarnQty;

    @Column(name = "status")
    private Integer status = 1;

    public Long getId() {
        return id;
    }

    public Long getSpuId() {
        return spuId;
    }

    public void setSpuId(Long spuId) {
        this.spuId = spuId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSpecJson() {
        return specJson;
    }

    public void setSpecJson(String specJson) {
        this.specJson = specJson;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public Integer getStockWarnQty() {
        return stockWarnQty;
    }

    public void setStockWarnQty(Integer stockWarnQty) {
        this.stockWarnQty = stockWarnQty;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

