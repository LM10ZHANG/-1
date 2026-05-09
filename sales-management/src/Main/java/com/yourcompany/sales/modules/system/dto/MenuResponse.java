package com.yourcompany.sales.modules.system.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class MenuResponse {
    private Long id;
    private Long parentId;
    private String menuName;
    private String menuType;
    private String path;
    private String component;
    private String permissionCode;
    private Integer sortNo;
    private Integer status;
    @Builder.Default
    private List<MenuResponse> children = new ArrayList<>();
}
