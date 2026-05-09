package com.yourcompany.sales.modules.system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "operation_log")
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_name", length = 50)
    private String moduleName;

    @Column(name = "action_name", length = 50)
    private String actionName;

    @Column(name = "biz_type", length = 50)
    private String bizType;

    @Column(name = "operator_user_id")
    private Long operatorUserId;

    @Column(name = "operator_username", length = 50)
    private String operatorUsername;

    @Column(name = "request_method", length = 10)
    private String requestMethod;

    @Column(name = "request_uri", length = 255)
    private String requestUri;

    @Column(name = "client_ip", length = 64)
    private String clientIp;

    @Column(name = "trace_id", length = 64)
    private String traceId;

    @Column(name = "success_flag")
    private Integer successFlag;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "cost_ms")
    private Long costMs;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
