package com.salesms.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

    @Value("${app.dummy-user-id:1}")
    private Long dummyUserId;

    public Long getUserId(HttpServletRequest request) {
        String header = request.getHeader("X-User-Id");
        if (header == null || header.isBlank()) {
            return dummyUserId;
        }
        try {
            return Long.parseLong(header.trim());
        } catch (Exception e) {
            return dummyUserId;
        }
    }
}

