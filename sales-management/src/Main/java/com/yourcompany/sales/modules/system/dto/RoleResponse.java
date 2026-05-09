package com.yourcompany.sales.modules.system.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RoleResponse {
    private Long id;
    private String roleCode;
    private String roleName;
    private Integer status;
    private String remark;
    private List<Long> menuIds;
}
