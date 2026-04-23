package com.yourcompany.sales.modules.dict.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 字典主新增/更新请求 DTO
 */
@Data
public class DictRequest {

    @NotBlank(message = "字典编码不能为空")
    @Size(max = 50)
    private String dictCode;

    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100)
    private String dictName;

    private Integer status = 1;

    private String remark;
}
