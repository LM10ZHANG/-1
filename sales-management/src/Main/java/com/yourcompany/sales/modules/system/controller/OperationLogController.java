package com.yourcompany.sales.modules.system.controller;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.modules.system.dto.OperationLogResponse;
import com.yourcompany.sales.modules.system.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs/operations")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    @GetMapping
    @PreAuthorize("hasAuthority('system:log:list')")
    public ApiResponse<PageResponse<OperationLogResponse>> page(@RequestParam(required = false) String moduleName,
                                                                @RequestParam(required = false) String username,
                                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(operationLogService.page(moduleName, username, pageNum, pageSize));
    }
}
