package com.yourcompany.sales.common.exception;

/**
 * 数据校验异常（参数校验失败时抛出）
 */
public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }

    public ValidationException(String field, String message) {
        super("VALIDATION_ERROR", String.format("字段[%s]校验失败: %s", field, message));
    }

    public static ValidationException invalidValue(String field, Object value, String reason) {
        return new ValidationException(field, String.format("值[%s]无效: %s", value, reason));
    }
}