package com.yourcompany.sales.common.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * 审计字段基类，所有业务表实体建议继承
 */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_flag")
    private Integer deletedFlag = 0;  // 0-未删除，1-已删除
}