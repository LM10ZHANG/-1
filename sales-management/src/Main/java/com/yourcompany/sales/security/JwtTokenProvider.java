package com.yourcompany.sales.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

    private final byte[] secretBytes;
    private final long expireSeconds;
    private final ObjectMapper objectMapper;

    public JwtTokenProvider(@Value("${sales.security.jwt-secret}") String secret,
                            @Value("${sales.security.token-expire-seconds:7200}") long expireSeconds,
                            ObjectMapper objectMapper) {
        this.secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.expireSeconds = expireSeconds;
        this.objectMapper = objectMapper;
    }

    public String createToken(LoginUser user) {
        long now = Instant.now().getEpochSecond();
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", user.getUsername());
        payload.put("userId", user.getUserId());
        payload.put("iat", now);
        payload.put("exp", now + expireSeconds);

        String headerPart = encodeJson(header);
        String payloadPart = encodeJson(payload);
        String content = headerPart + "." + payloadPart;
        return content + "." + sign(content);
    }

    public String getUsername(String token) {
        Map<String, Object> claims = parseClaims(token);
        Object subject = claims.get("sub");
        if (subject == null) {
            throw new BadCredentialsException("Token 无效");
        }
        return subject.toString();
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }

    private Map<String, Object> parseClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new BadCredentialsException("Token 格式错误");
            }
            String content = parts[0] + "." + parts[1];
            String expectedSignature = sign(content);
            if (!constantTimeEquals(expectedSignature, parts[2])) {
                throw new BadCredentialsException("Token 签名无效");
            }
            Map<String, Object> claims = objectMapper.readValue(
                    BASE64_URL_DECODER.decode(parts[1]),
                    new TypeReference<Map<String, Object>>() {
                    });
            Object exp = claims.get("exp");
            if (!(exp instanceof Number) || ((Number) exp).longValue() < Instant.now().getEpochSecond()) {
                throw new BadCredentialsException("Token 已过期");
            }
            return claims;
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new BadCredentialsException("Token 解析失败");
        }
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (Exception e) {
            throw new IllegalStateException("Token 生成失败", e);
        }
    }

    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretBytes, "HmacSHA256"));
            return BASE64_URL_ENCODER.encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Token 签名失败", e);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        byte[] left = a.getBytes(StandardCharsets.UTF_8);
        byte[] right = b.getBytes(StandardCharsets.UTF_8);
        if (left.length != right.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < left.length; i++) {
            result |= left[i] ^ right[i];
        }
        return result == 0;
    }
}
