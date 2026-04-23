package com.yourcompany.sales.modules.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类树节点（用于 /api/products/categories 返回一棵完整分类树）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryNode {

    private Long id;

    private Long parentId;

    private String categoryCode;

    private String categoryName;

    private Integer sortNo;

    private Integer status;

    private String remark;

    private List<CategoryNode> children = new ArrayList<>();
}
