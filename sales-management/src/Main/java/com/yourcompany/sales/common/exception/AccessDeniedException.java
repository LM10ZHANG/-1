package com.yourcompany.sales.common.exception;

/**
 * 权限不足异常
 */
public class AccessDeniedException extends BusinessException {

    public AccessDeniedException(String message) {
        super("ACCESS_DENIED", message);
    }

    public AccessDeniedException() {
        super("ACCESS_DENIED", "您没有权限执行此操作");
    }
}