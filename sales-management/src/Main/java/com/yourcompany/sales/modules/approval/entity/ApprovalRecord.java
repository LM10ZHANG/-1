package com.yourcompany.sales.modules.approval.entity;

import com.yourcompany.sales.common.enums.ApprovalAction;
import com.yourcompany.sales.common.enums.BizType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 审批记录实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "approval_record")
public class ApprovalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "biz_type", length = 30, nullable = false)
    private BizType bizType;            // 业务类型：QUOTE / ORDER / REFUND

    @Column(name = "biz_id", nullable = false)
    private Long bizId;                 // 业务主键ID

    @Column(name = "biz_no", length = 50)
    private String bizNo;               // 业务单号（冗余，便于查询）

    @Column(name = "apply_user_id")
    private Long applyUserId;           // 发起人

    @Column(name = "approver_user_id")
    private Long approverUserId;        // 审批人

    @Enumerated(EnumType.STRING)
    @Column(name = "action", length = 20)
    private ApprovalAction action;      // 审批动作

    @Column(name = "comment", length = 255)
    private String comment;             // 审批意见

    @Column(name = "action_time")
    private LocalDateTime actionTime;   // 审批时间

    @Column(name = "status_after_action", length = 20)
    private String statusAfterAction;   // 审批后业务单状态
}