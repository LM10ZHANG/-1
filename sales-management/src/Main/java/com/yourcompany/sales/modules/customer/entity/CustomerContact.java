package com.yourcompany.sales.modules.customer.entity;

import com.yourcompany.sales.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 客户联系人实体（对应文档 7.6 customer_contact）
 */
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "customer_contact")
public class CustomerContact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "mobile", length = 20)
    private String mobile;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "position", length = 50)
    private String position;

    @Column(name = "wechat", length = 50)
    private String wechat;

    @Column(name = "is_primary", nullable = false)
    private Integer isPrimary = 0;           // 1 主联系人 / 0 非主

    @Column(name = "remark", length = 255)
    private String remark;
}
