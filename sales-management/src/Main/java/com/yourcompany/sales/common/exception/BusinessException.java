package com.yourcompany.sales.common.exception;

import lombok.Getter;

/**
 * 业务异常基类
 * 所有业务层抛出的异常都应使用此类或其子类
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final transient Object[] args;

    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
        this.args = null;
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = null;
    }

    public BusinessException(String errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }

    // ---------- 常用静态工厂方法 ----------

    /**
     * 资源不存在
     */
    public static BusinessException notFound(String resource, Object id) {
        return new BusinessException("NOT_FOUND", resource + " 不存在: " + id);
    }

    /**
     * 状态不正确，无法执行操作
     */
    public static BusinessException invalidStatus(String currentStatus, String expectedStatus) {
        return new BusinessException("INVALID_STATUS",
                String.format("当前状态[%s]不允许该操作，期望状态[%s]", currentStatus, expectedStatus));
    }

    /**
     * 操作被禁止
     */
    public static BusinessException operationForbidden(String reason) {
        return new BusinessException("FORBIDDEN", reason);
    }

    /**
     * 数据已存在/重复
     */
    public static BusinessException alreadyExists(String resource, String field, Object value) {
        return new BusinessException("ALREADY_EXISTS",
                String.format("%s 的 %s 已存在: %s", resource, field, value));
    }
}