package com.salesms.product.entity;

import com.salesms.common.AuditFields;
import jakarta.persistence.*;

@Entity
@Table(name = "product_category")
public class ProductCategory extends AuditFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_code", length = 50, unique = true)
    private String categoryCode;

    @Column(name = "category_name", length = 120, nullable = false)
    private String categoryName;

    public Long getId() {
        return id;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}

