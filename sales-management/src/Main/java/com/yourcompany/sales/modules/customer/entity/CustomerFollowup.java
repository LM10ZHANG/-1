package com.yourcompany.sales.modules.customer.entity;

import com.yourcompany.sales.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 客户跟进记录实体（对应文档 7.7 customer_followup）
 */
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "customer_followup")
public class CustomerFollowup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "follow_user_id")
    private Long followUserId;

    @Column(name = "follow_type", length = 20)
    private String followType;               // 电话/拜访/微信/邮件

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "next_follow_time")
    private LocalDateTime nextFollowTime;

    @Column(name = "follow_result", length = 50)
    private String followResult;
}
