package com.yourcompany.sales.modules.product.controller;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.modules.product.dto.CategoryNode;
import com.yourcompany.sales.modules.product.dto.CategoryRequest;
import com.yourcompany.sales.modules.product.entity.ProductCategory;
import com.yourcompany.sales.modules.product.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 *
 * 对应分工文档 4.2 API：
 *  - GET  /api/products/categories
 */
@RestController
@RequestMapping("/api/products/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 查询完整分类树
     */
    @GetMapping
    public ApiResponse<List<CategoryNode>> tree() {
        return ApiResponse.success(categoryService.tree());
    }

    @PostMapping
    public ApiResponse<ProductCategory> create(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success(categoryService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductCategory> update(@PathVariable Long id,
                                               @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success(categoryService.update(id, request));
    }
}
