package com.yourcompany.sales.modules.product.controller;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.modules.product.dto.SpuQueryRequest;
import com.yourcompany.sales.modules.product.dto.SpuRequest;
import com.yourcompany.sales.modules.product.dto.SpuResponse;
import com.yourcompany.sales.modules.product.service.SpuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品 SPU 控制器
 *
 * 对应分工文档 4.2 API：
 *  - GET  /api/products/spu
 *  - POST /api/products/spu
 *  - PUT  /api/products/spu/{id}
 */
@RestController
@RequestMapping("/api/products/spu")
@RequiredArgsConstructor
public class SpuController {

    private final SpuService spuService;

    @PostMapping
    public ApiResponse<SpuResponse> create(@Valid @RequestBody SpuRequest request) {
        return ApiResponse.success(spuService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<SpuResponse> update(@PathVariable Long id,
                                           @Valid @RequestBody SpuRequest request) {
        return ApiResponse.success(spuService.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<SpuResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(spuService.getById(id));
    }

    @GetMapping
    public ApiResponse<PageResponse<SpuResponse>> page(SpuQueryRequest query) {
        return ApiResponse.success(spuService.page(query));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        spuService.changeStatus(id, status);
        return ApiResponse.success();
    }
}
