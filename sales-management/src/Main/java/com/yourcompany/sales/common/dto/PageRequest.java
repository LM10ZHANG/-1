package com.yourcompany.sales.common.dto;

import lombok.Data;

import jakarta.validation.constraints.Min;

/**
 * 分页查询请求基类
 */
@Data
public class PageRequest {

    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数最小为1")
    private Integer pageSize = 10;

    private String orderBy;      // 排序字段（例如 "createdAt"）
    private Boolean asc = true;  // 是否升序

    /**
     * 计算偏移量（用于 SQL 的 LIMIT offset, pageSize）
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}