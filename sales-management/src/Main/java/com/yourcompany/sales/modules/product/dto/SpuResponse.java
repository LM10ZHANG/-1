package com.yourcompany.sales.modules.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SPU 响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpuResponse {

    private Long id;

    private String spuCode;

    private String spuName;

    private Long categoryId;

    private String categoryName;

    private String brandName;

    private String unitName;

    private String description;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
