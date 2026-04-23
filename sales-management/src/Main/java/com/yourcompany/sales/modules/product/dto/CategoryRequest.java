package com.yourcompany.sales.modules.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 商品分类新增/更新请求 DTO
 */
@Data
public class CategoryRequest {

    /** 父分类 ID，0 表示根 */
    private Long parentId = 0L;

    @NotBlank(message = "分类编码不能为空")
    @Size(max = 50)
    private String categoryCode;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100)
    private String categoryName;

    private Integer sortNo = 0;

    private Integer status = 1;

    private String remark;
}
