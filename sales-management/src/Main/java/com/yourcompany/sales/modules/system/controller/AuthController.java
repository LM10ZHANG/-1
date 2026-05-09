package com.yourcompany.sales.modules.system.controller;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.modules.system.dto.LoginRequest;
import com.yourcompany.sales.modules.system.dto.LoginResponse;
import com.yourcompany.sales.modules.system.log.OperationLogRecord;
import com.yourcompany.sales.modules.system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @OperationLogRecord(module = "认证", action = "登录", bizType = "AUTH_LOGIN")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }
}
