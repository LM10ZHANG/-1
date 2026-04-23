package com.yourcompany.sales.modules.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * SPU 新增/更新请求 DTO
 */
@Data
public class SpuRequest {

    @NotBlank(message = "SPU 编码不能为空")
    @Size(max = 50)
    private String spuCode;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 120)
    private String spuName;

    private Long categoryId;

    private String brandName;

    private String unitName;

    private String description;

    /** 默认启用 */
    private Integer status = 1;
}
