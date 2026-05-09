package com.yourcompany.sales.modules.system.controller;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.modules.system.dto.MenuRequest;
import com.yourcompany.sales.modules.system.dto.MenuResponse;
import com.yourcompany.sales.modules.system.log.OperationLogRecord;
import com.yourcompany.sales.modules.system.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    @PreAuthorize("hasAuthority('system:menu:list')")
    public ApiResponse<List<MenuResponse>> tree() {
        return ApiResponse.success(menuService.tree());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:create')")
    @OperationLogRecord(module = "菜单管理", action = "新增菜单", bizType = "SYS_MENU")
    public ApiResponse<MenuResponse> create(@Valid @RequestBody MenuRequest request) {
        return ApiResponse.success(menuService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:update')")
    @OperationLogRecord(module = "菜单管理", action = "更新菜单", bizType = "SYS_MENU")
    public ApiResponse<MenuResponse> update(@PathVariable Long id, @Valid @RequestBody MenuRequest request) {
        return ApiResponse.success(menuService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    @OperationLogRecord(module = "菜单管理", action = "删除菜单", bizType = "SYS_MENU")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return ApiResponse.success();
    }
}
