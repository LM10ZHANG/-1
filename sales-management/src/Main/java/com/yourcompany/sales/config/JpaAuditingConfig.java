package com.yourcompany.sales.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 启用 JPA 审计功能，配合 @CreatedDate、@LastModifiedDate 等注解自动填充时间
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}