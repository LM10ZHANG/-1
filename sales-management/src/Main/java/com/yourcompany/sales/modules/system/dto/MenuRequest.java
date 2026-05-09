package com.yourcompany.sales.modules.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MenuRequest {
    private Long parentId = 0L;
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;
    @NotBlank(message = "菜单类型不能为空")
    private String menuType;
    private String path;
    private String component;
    private String permissionCode;
    private Integer sortNo = 0;
    private Integer status = 1;
}
