package com.salesms.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String code;
    private String message;
    private T data;
    private String traceId;

    private ApiResponse() {
    }

    public static <T> ApiResponse<T> success(String traceId, T data) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.code = "0";
        resp.message = "success";
        resp.data = data;
        resp.traceId = traceId;
        return resp;
    }

    public static <T> ApiResponse<T> fail(String traceId, String code, String message) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.code = code;
        resp.message = message;
        resp.traceId = traceId;
        return resp;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getTraceId() {
        return traceId;
    }
}

