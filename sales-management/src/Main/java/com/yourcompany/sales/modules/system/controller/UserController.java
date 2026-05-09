package com.yourcompany.sales.modules.system.controller;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.modules.system.dto.UserRequest;
import com.yourcompany.sales.modules.system.dto.UserResponse;
import com.yourcompany.sales.modules.system.log.OperationLogRecord;
import com.yourcompany.sales.modules.system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('system:user:list')")
    public ApiResponse<PageResponse<UserResponse>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(userService.page(pageNum, pageSize));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:user:create')")
    @OperationLogRecord(module = "用户管理", action = "新增用户", bizType = "SYS_USER")
    public ApiResponse<UserResponse> create(@Valid @RequestBody UserRequest request) {
        return ApiResponse.success(userService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:update')")
    @OperationLogRecord(module = "用户管理", action = "更新用户", bizType = "SYS_USER")
    public ApiResponse<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return ApiResponse.success(userService.update(id, request));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:user:status')")
    @OperationLogRecord(module = "用户管理", action = "修改用户状态", bizType = "SYS_USER")
    public ApiResponse<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.changeStatus(id, status);
        return ApiResponse.success();
    }
}
