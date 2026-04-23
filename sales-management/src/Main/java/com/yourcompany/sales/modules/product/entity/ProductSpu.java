package com.yourcompany.sales.modules.product.entity;

import com.yourcompany.sales.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 商品 SPU 实体（对应文档 7.8 product_spu）
 */
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "product_spu")
public class ProductSpu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spu_code", unique = true, nullable = false, length = 50)
    private String spuCode;

    @Column(name = "spu_name", nullable = false, length = 120)
    private String spuName;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "brand_name", length = 50)
    private String brandName;

    @Column(name = "unit_name", length = 20)
    private String unitName;

    @Column(name = "status", nullable = false)
    private Integer status = 1;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
