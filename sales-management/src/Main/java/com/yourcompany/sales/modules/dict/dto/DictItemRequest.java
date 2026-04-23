package com.yourcompany.sales.modules.dict.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 字典项新增/更新请求 DTO
 */
@Data
public class DictItemRequest {

    @NotBlank(message = "字典项值不能为空")
    @Size(max = 50)
    private String itemValue;

    @NotBlank(message = "字典项显示文本不能为空")
    @Size(max = 100)
    private String itemLabel;

    private Integer sortNo = 0;

    private Integer status = 1;

    private String remark;
}
