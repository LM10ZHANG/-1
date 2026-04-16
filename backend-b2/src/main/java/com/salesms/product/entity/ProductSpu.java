package com.salesms.product.entity;

import com.salesms.common.AuditFields;
import jakarta.persistence.*;

@Entity
@Table(name = "product_spu")
public class ProductSpu extends AuditFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spu_code", length = 50, nullable = false, unique = true)
    private String spuCode;

    @Column(name = "spu_name", length = 120, nullable = false)
    private String spuName;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "brand_name", length = 50)
    private String brandName;

    @Column(name = "unit_name", length = 20)
    private String unitName;

    @Column(name = "status")
    private Integer status = 1;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public Long getId() {
        return id;
    }

    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode;
    }

    public String getSpuName() {
        return spuName;
    }

    public void setSpuName(String spuName) {
        this.spuName = spuName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

