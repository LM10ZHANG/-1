package com.yourcompany.sales.config;

import com.yourcompany.sales.utils.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class ApiAccessLogFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri == null || !uri.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long start = System.currentTimeMillis();
        Throwable error = null;
        try {
            filterChain.doFilter(request, response);
        } catch (Throwable ex) {
            error = ex;
            throw ex;
        } finally {
            long costMs = System.currentTimeMillis() - start;
            writeAccessLog(request, response, costMs, error);
        }
    }

    private void writeAccessLog(HttpServletRequest request, HttpServletResponse response, long costMs, Throwable error) {
        String queryString = request.getQueryString();
        String uri = request.getRequestURI();
        String fullPath = queryString == null || queryString.isBlank() ? uri : uri + "?" + queryString;
        Long userId = SecurityUtils.getCurrentUserId();
        String username = SecurityUtils.getCurrentUsername();
        String traceId = MDC.get("traceId");
        String clientIp = resolveIp(request);
        int status = response.getStatus();

        if (error == null) {
            log.info("api_access method={} path={} status={} userId={} username={} ip={} traceId={} costMs={}",
                    request.getMethod(), fullPath, status, userId, username, clientIp, traceId, costMs);
        } else {
            log.warn("api_access method={} path={} status={} userId={} username={} ip={} traceId={} costMs={} error={}",
                    request.getMethod(), fullPath, status, userId, username, clientIp, traceId, costMs, error.getMessage());
        }
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }
}
