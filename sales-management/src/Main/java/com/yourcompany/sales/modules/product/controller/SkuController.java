package com.yourcompany.sales.modules.product.controller;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.modules.product.dto.SkuQueryRequest;
import com.yourcompany.sales.modules.product.dto.SkuRequest;
import com.yourcompany.sales.modules.product.dto.SkuResponse;
import com.yourcompany.sales.modules.product.service.SkuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品 SKU 控制器
 *
 * 对应分工文档 4.2 API：
 *  - GET  /api/products/sku
 *  - POST /api/products/sku
 *  - PUT  /api/products/sku/{id}
 */
@RestController
@RequestMapping("/api/products/sku")
@RequiredArgsConstructor
public class SkuController {

    private final SkuService skuService;

    @PostMapping
    public ApiResponse<SkuResponse> create(@Valid @RequestBody SkuRequest request) {
        return ApiResponse.success(skuService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<SkuResponse> update(@PathVariable Long id,
                                           @Valid @RequestBody SkuRequest request) {
        return ApiResponse.success(skuService.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<SkuResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(skuService.getById(id));
    }

    @GetMapping
    public ApiResponse<PageResponse<SkuResponse>> page(SkuQueryRequest query) {
        return ApiResponse.success(skuService.page(query));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        skuService.changeStatus(id, status);
        return ApiResponse.success();
    }
}
