package com.yourcompany.sales.modules.system.controller;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.modules.system.dto.RoleMenuRequest;
import com.yourcompany.sales.modules.system.dto.RoleRequest;
import com.yourcompany.sales.modules.system.dto.RoleResponse;
import com.yourcompany.sales.modules.system.log.OperationLogRecord;
import com.yourcompany.sales.modules.system.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority('system:role:list')")
    public ApiResponse<List<RoleResponse>> list() {
        return ApiResponse.success(roleService.list());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:role:create')")
    @OperationLogRecord(module = "角色管理", action = "新增角色", bizType = "SYS_ROLE")
    public ApiResponse<RoleResponse> create(@Valid @RequestBody RoleRequest request) {
        return ApiResponse.success(roleService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:update')")
    @OperationLogRecord(module = "角色管理", action = "更新角色", bizType = "SYS_ROLE")
    public ApiResponse<RoleResponse> update(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return ApiResponse.success(roleService.update(id, request));
    }

    @PutMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('system:role:menus')")
    @OperationLogRecord(module = "角色管理", action = "分配菜单权限", bizType = "SYS_ROLE_MENU")
    public ApiResponse<Void> updateMenus(@PathVariable Long id, @RequestBody RoleMenuRequest request) {
        roleService.updateMenus(id, request);
        return ApiResponse.success();
    }
}
