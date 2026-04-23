package com.yourcompany.sales.modules.customer.entity;

import com.yourcompany.sales.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 客户主表实体（对应文档 7.5 customer）
 */
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "customer")
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_code", unique = true, nullable = false, length = 50)
    private String customerCode;

    @Column(name = "customer_name", nullable = false, length = 120)
    private String customerName;

    @Column(name = "customer_level", length = 20)
    private String customerLevel;            // A/B/C 或会员等级

    @Column(name = "customer_type", length = 20)
    private String customerType;             // 企业/个人/渠道

    @Column(name = "industry", length = 50)
    private String industry;

    @Column(name = "source", length = 50)
    private String source;

    @Column(name = "province", length = 50)
    private String province;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "owner_user_id")
    private Long ownerUserId;                // 负责人

    @Column(name = "credit_limit", precision = 18, scale = 2)
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @Column(name = "current_ar_amount", precision = 18, scale = 2)
    private BigDecimal currentArAmount = BigDecimal.ZERO;

    @Column(name = "follow_status", length = 20)
    private String followStatus;

    @Column(name = "status", nullable = false)
    private Integer status = 1;              // 1 正常 / 0 禁用

    @Column(name = "remark", length = 255)
    private String remark;
}
