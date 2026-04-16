package com.salesms.product.controller;

import com.salesms.common.ApiResponse;
import com.salesms.common.PageResult;
import com.salesms.common.TraceIdContext;
import com.salesms.common.UserContext;
import com.salesms.product.dto.*;
import com.salesms.product.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final UserContext userContext;

    public ProductController(ProductService productService, UserContext userContext) {
        this.productService = productService;
        this.userContext = userContext;
    }

    @GetMapping("/spu")
    public ApiResponse<PageResult<ProductSpuResponse>> listSpu(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        PageResult<ProductSpuResponse> result = productService.listSpu(keyword, categoryId, status, page, size);
        return ApiResponse.success(TraceIdContext.get(), result);
    }

    @PostMapping("/spu")
    public ApiResponse<Long> createSpu(
            @Valid @RequestBody ProductSpuCreateRequest req,
            HttpServletRequest request
    ) {
        Long operatorUserId = userContext.getUserId(request);
        Long id = productService.createSpu(req, operatorUserId);
        return ApiResponse.success(TraceIdContext.get(), id);
    }

    @GetMapping("/sku")
    public ApiResponse<PageResult<ProductSkuResponse>> listSku(
            @RequestParam(value = "spuId", required = false) Long spuId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        PageResult<ProductSkuResponse> result = productService.listSku(spuId, status, keyword, page, size);
        return ApiResponse.success(TraceIdContext.get(), result);
    }

    @PostMapping("/sku")
    public ApiResponse<Long> createSku(
            @Valid @RequestBody ProductSkuCreateRequest req,
            HttpServletRequest request
    ) {
        Long operatorUserId = userContext.getUserId(request);
        Long id = productService.createSku(req, operatorUserId);
        return ApiResponse.success(TraceIdContext.get(), id);
    }

    @PutMapping("/sku/{id}")
    public ApiResponse<Long> updateSku(
            @PathVariable("id") Long id,
            @Valid @RequestBody ProductSkuCreateRequest req,
            HttpServletRequest request
    ) {
        Long operatorUserId = userContext.getUserId(request);
        Long skuId = productService.updateSku(id, req, operatorUserId);
        return ApiResponse.success(TraceIdContext.get(), skuId);
    }

    @GetMapping("/categories")
    public ApiResponse<PageResult<ProductCategoryResponse>> listCategories(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        PageResult<ProductCategoryResponse> result = productService.listCategories(page, size);
        return ApiResponse.success(TraceIdContext.get(), result);
    }
}

