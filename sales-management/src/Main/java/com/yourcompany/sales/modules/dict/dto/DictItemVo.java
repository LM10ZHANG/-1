package com.yourcompany.sales.modules.dict.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字典项响应 VO —— 前端下拉使用 value/label 对
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictItemVo {

    private Long id;

    private String dictCode;

    private String itemValue;

    private String itemLabel;

    private Integer sortNo;

    private Integer status;

    private String remark;
}
