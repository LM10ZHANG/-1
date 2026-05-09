package com.yourcompany.sales.modules.system.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OperationLogResponse {
    private Long id;
    private String moduleName;
    private String actionName;
    private String bizType;
    private Long operatorUserId;
    private String operatorUsername;
    private String requestMethod;
    private String requestUri;
    private String clientIp;
    private String traceId;
    private Integer successFlag;
    private String errorMessage;
    private Long costMs;
    private LocalDateTime createdAt;
}
