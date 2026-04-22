package com.yourcompany.sales.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * 分页查询响应
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private Long total;          // 总记录数
    private Integer pageNum;     // 当前页码
    private Integer pageSize;    // 每页条数
    private Integer pages;       // 总页数
    private List<T> list;        // 数据列表

    /**
     * 构造分页响应对象
     */
    public static <T> PageResponse<T> of(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        int pages = (int) Math.ceil((double) total / pageSize);
        return PageResponse.<T>builder()
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .pages(pages)
                .list(list)
                .build();
    }
}