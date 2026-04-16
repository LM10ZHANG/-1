package com.salesms.product.service;

import com.salesms.common.BusinessException;
import com.salesms.common.PageResult;
import com.salesms.product.dto.*;
import com.salesms.product.entity.ProductCategory;
import com.salesms.product.entity.ProductSpu;
import com.salesms.product.entity.ProductSku;
import com.salesms.product.repo.ProductCategoryRepository;
import com.salesms.product.repo.ProductSpuRepository;
import com.salesms.product.repo.ProductSkuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductCategoryRepository categoryRepository;
    private final ProductSpuRepository spuRepository;
    private final ProductSkuRepository skuRepository;

    public ProductService(
            ProductCategoryRepository categoryRepository,
            ProductSpuRepository spuRepository,
            ProductSkuRepository skuRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.spuRepository = spuRepository;
        this.skuRepository = skuRepository;
    }

    @Transactional
    public Long createSpu(ProductSpuCreateRequest req, Long operatorUserId) {
        Integer deletedFlag = 0;
        if (spuRepository.existsBySpuCodeAndDeletedFlag(req.getSpuCode().trim(), deletedFlag)) {
            throw new BusinessException("SPU_CODE_DUPLICATE", "spu_code already exists");
        }

        ProductSpu spu = new ProductSpu();
        spu.setSpuCode(req.getSpuCode().trim());
        spu.setSpuName(req.getSpuName().trim());
        spu.setCategoryId(req.getCategoryId());
        spu.setBrandName(req.getBrandName());
        spu.setUnitName(req.getUnitName());
        spu.setStatus(req.getStatus() == null ? 1 : req.getStatus());
        spu.setDescription(req.getDescription());

        spu.setDeletedFlag(0);
        spu.setCreatedBy(operatorUserId);
        spu.setCreatedAt(LocalDateTime.now());
        spu.setUpdatedBy(operatorUserId);
        spu.setUpdatedAt(LocalDateTime.now());

        return spuRepository.save(spu).getId();
    }

    @Transactional(readOnly = true)
    public PageResult<ProductSpuResponse> listSpu(String keyword, Long categoryId, Integer status, int page, int size) {
        Integer deletedFlag = 0;
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductSpu> result = spuRepository.search(deletedFlag, categoryId, status, keyword, pageable);

        PageResult<ProductSpuResponse> out = new PageResult<>();
        out.setPage(page);
        out.setSize(size);
        out.setTotal(result.getTotalElements());
        out.setRecords(new ArrayList<>());

        for (ProductSpu p : result.getContent()) {
            ProductSpuResponse r = new ProductSpuResponse();
            r.setId(p.getId());
            r.setSpuCode(p.getSpuCode());
            r.setSpuName(p.getSpuName());
            r.setCategoryId(p.getCategoryId());
            r.setBrandName(p.getBrandName());
            r.setUnitName(p.getUnitName());
            r.setStatus(p.getStatus());
            r.setDescription(p.getDescription());
            out.getRecords().add(r);
        }
        return out;
    }

    @Transactional
    public Long createSku(ProductSkuCreateRequest req, Long operatorUserId) {
        Integer deletedFlag = 0;

        if (skuRepository.existsBySkuCodeAndDeletedFlag(req.getSkuCode().trim(), deletedFlag)) {
            throw new BusinessException("SKU_CODE_DUPLICATE", "sku_code already exists");
        }

        // SKU 必须引用存在的 SPU（B 模块职责内，先做最基本的校验）
        ProductSpu spu = spuRepository.findByIdAndDeletedFlag(req.getSpuId(), deletedFlag)
                .orElseThrow(() -> new BusinessException("SPU_NOT_FOUND", "spu not found"));

        ProductSku sku = new ProductSku();
        sku.setSpuId(spu.getId());
        sku.setSkuCode(req.getSkuCode().trim());
        sku.setSkuName(req.getSkuName().trim());
        sku.setSpecJson(req.getSpecJson());
        sku.setBarcode(req.getBarcode());
        sku.setSalePrice(req.getSalePrice());
        sku.setCostPrice(req.getCostPrice());
        sku.setTaxRate(req.getTaxRate());
        sku.setStockWarnQty(req.getStockWarnQty());
        sku.setStatus(req.getStatus() == null ? 1 : req.getStatus());

        sku.setDeletedFlag(0);
        sku.setCreatedBy(operatorUserId);
        sku.setCreatedAt(LocalDateTime.now());
        sku.setUpdatedBy(operatorUserId);
        sku.setUpdatedAt(LocalDateTime.now());

        return skuRepository.save(sku).getId();
    }

    @Transactional(readOnly = true)
    public PageResult<ProductSkuResponse> listSku(Long spuId, Integer status, String keyword, int page, int size) {
        Integer deletedFlag = 0;
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductSku> result = skuRepository.search(deletedFlag, spuId, status, keyword, pageable);

        PageResult<ProductSkuResponse> out = new PageResult<>();
        out.setPage(page);
        out.setSize(size);
        out.setTotal(result.getTotalElements());
        out.setRecords(new ArrayList<>());

        for (ProductSku s : result.getContent()) {
            ProductSkuResponse r = new ProductSkuResponse();
            r.setId(s.getId());
            r.setSpuId(s.getSpuId());
            r.setSkuCode(s.getSkuCode());
            r.setSkuName(s.getSkuName());
            r.setSpecJson(s.getSpecJson());
            r.setBarcode(s.getBarcode());
            r.setSalePrice(s.getSalePrice());
            r.setCostPrice(s.getCostPrice());
            r.setTaxRate(s.getTaxRate());
            r.setStockWarnQty(s.getStockWarnQty());
            r.setStatus(s.getStatus());
            out.getRecords().add(r);
        }
        return out;
    }

    @Transactional
    public Long updateSku(Long skuId, ProductSkuCreateRequest req, Long operatorUserId) {
        Integer deletedFlag = 0;
        ProductSku sku = skuRepository.findByIdAndDeletedFlag(skuId, deletedFlag)
                .orElseThrow(() -> new BusinessException("SKU_NOT_FOUND", "sku not found"));

        // 允许调整引用的 SPU（做基本存在性校验）
        ProductSpu spu = spuRepository.findByIdAndDeletedFlag(req.getSpuId(), deletedFlag)
                .orElseThrow(() -> new BusinessException("SPU_NOT_FOUND", "spu not found"));

        String newSkuCode = req.getSkuCode().trim();
        if (!newSkuCode.equalsIgnoreCase(sku.getSkuCode()) &&
                skuRepository.existsBySkuCodeAndDeletedFlag(newSkuCode, deletedFlag)) {
            throw new BusinessException("SKU_CODE_DUPLICATE", "sku_code already exists");
        }

        sku.setSpuId(spu.getId());
        sku.setSkuCode(newSkuCode);
        sku.setSkuName(req.getSkuName().trim());
        sku.setSpecJson(req.getSpecJson());
        sku.setBarcode(req.getBarcode());
        sku.setSalePrice(req.getSalePrice());
        sku.setCostPrice(req.getCostPrice());
        sku.setTaxRate(req.getTaxRate());
        sku.setStockWarnQty(req.getStockWarnQty());
        sku.setStatus(req.getStatus() == null ? 1 : req.getStatus());

        sku.setUpdatedBy(operatorUserId);
        sku.setUpdatedAt(LocalDateTime.now());

        return skuRepository.save(sku).getId();
    }

    @Transactional(readOnly = true)
    public PageResult<ProductCategoryResponse> listCategories(int page, int size) {
        Integer deletedFlag = 0;
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductCategory> result = categoryRepository.findAllByDeletedFlag(deletedFlag, pageable);

        PageResult<ProductCategoryResponse> out = new PageResult<>();
        out.setPage(page);
        out.setSize(size);
        out.setTotal(result.getTotalElements());
        out.setRecords(new ArrayList<>());

        for (ProductCategory c : result.getContent()) {
            ProductCategoryResponse r = new ProductCategoryResponse();
            r.setId(c.getId());
            r.setCategoryCode(c.getCategoryCode());
            r.setCategoryName(c.getCategoryName());
            out.getRecords().add(r);
        }
        return out;
    }
}

