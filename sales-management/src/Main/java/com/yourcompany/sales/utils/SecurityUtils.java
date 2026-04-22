package com.yourcompany.sales.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 安全工具类：获取当前登录用户信息
 */
@Component
public class SecurityUtils {

    /**
     * 获取当前登录用户ID
     * 注意：这个方法需要根据你们实际的身份认证实现来调整
     * 这里提供一个示例，假设 UserDetails 中有 getId() 方法
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        // 如果你们使用了 Spring Security，并且 UserDetails 实现了自定义接口
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        // 临时方案：在没有完整认证时返回一个默认用户ID用于开发测试
        // 生产环境必须删除此行，改为抛出异常或从真实用户获取
        return 1L;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "system";
        }
        return authentication.getName();
    }
}