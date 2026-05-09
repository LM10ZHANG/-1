package com.yourcompany.sales.modules.system.log;

import com.yourcompany.sales.modules.system.entity.OperationLog;
import com.yourcompany.sales.modules.system.repository.OperationLogRepository;
import com.yourcompany.sales.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogRepository operationLogRepository;

    @Around("@annotation(record)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLogRecord record) throws Throwable {
        long start = System.currentTimeMillis();
        OperationLog log = buildBaseLog(record);
        try {
            Object result = joinPoint.proceed();
            log.setSuccessFlag(1);
            return result;
        } catch (Throwable ex) {
            log.setSuccessFlag(0);
            log.setErrorMessage(ex.getMessage());
            throw ex;
        } finally {
            log.setCostMs(System.currentTimeMillis() - start);
            operationLogRepository.save(log);
        }
    }

    private OperationLog buildBaseLog(OperationLogRecord record) {
        OperationLog log = new OperationLog();
        log.setModuleName(record.module());
        log.setActionName(record.action());
        log.setBizType(record.bizType());
        log.setOperatorUserId(SecurityUtils.getCurrentUserId());
        log.setOperatorUsername(SecurityUtils.getCurrentUsername());
        log.setTraceId(MDC.get("traceId"));
        log.setCreatedAt(LocalDateTime.now());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.setRequestMethod(request.getMethod());
            log.setRequestUri(request.getRequestURI());
            log.setClientIp(resolveIp(request));
        }
        return log;
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
