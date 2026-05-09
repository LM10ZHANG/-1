package com.yourcompany.sales.modules.system.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RoleMenuRequest {
    private List<Long> menuIds = new ArrayList<>();
}
