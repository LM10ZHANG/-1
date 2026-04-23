package com.yourcompany.sales.modules.product.service;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.product.dto.SkuQueryRequest;
import com.yourcompany.sales.modules.product.dto.SkuRequest;
import com.yourcompany.sales.modules.product.dto.SkuResponse;
import com.yourcompany.sales.modules.product.entity.ProductSku;
import com.yourcompany.sales.modules.product.entity.ProductSpu;
import com.yourcompany.sales.modules.product.repository.ProductSkuRepository;
import com.yourcompany.sales.modules.product.repository.ProductSpuRepository;
import com.yourcompany.sales.utils.BeanCopyUtils;
import com.yourcompany.sales.utils.SecurityUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SKU 业务服务
 *
 * 业务规则（《需求文档》4.4）：
 * 1. 停用 SKU 后不可创建新订单，但历史订单仍可查看（历史查询走 getSnapshot）
 * 2. 订单/报价明细必须引用 SKU，不应直接写死商品名称
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SkuService {

    private final ProductSkuRepository skuRepository;
    private final ProductSpuRepository spuRepository;

    @Transactional
    public SkuResponse create(SkuRequest request) {
        if (skuRepository.existsBySkuCode(request.getSkuCode())) {
            throw BusinessException.alreadyExists("SKU", "编码", request.getSkuCode());
        }
        // 校验 SPU 存在
        spuRepository.findById(request.getSpuId())
                .orElseThrow(() -> BusinessException.notFound("SPU", request.getSpuId()));

        ProductSku sku = new ProductSku();
        BeanUtils.copyProperties(request, sku);
        if (sku.getStatus() == null) sku.setStatus(1);
        sku.setCreatedBy(SecurityUtils.getCurrentUserId());
        sku.setCreatedAt(LocalDateTime.now());
        ProductSku saved = skuRepository.save(sku);
        log.info("新增 SKU 成功, id={}, code={}", saved.getId(), saved.getSkuCode());
        return toResponse(saved);
    }

    @Transactional
    public SkuResponse update(Long id, SkuRequest request) {
        ProductSku sku = skuRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("SKU", id));
        sku.setSkuName(request.getSkuName());
        sku.setSpecJson(request.getSpecJson());
        sku.setBarcode(request.getBarcode());
        sku.setSalePrice(request.getSalePrice());
        sku.setCostPrice(request.getCostPrice());
        sku.setTaxRate(request.getTaxRate());
        sku.setStockWarnQty(request.getStockWarnQty());
        if (request.getStatus() != null) {
            sku.setStatus(request.getStatus());
        }
        sku.setUpdatedBy(SecurityUtils.getCurrentUserId());
        sku.setUpdatedAt(LocalDateTime.now());
        return toResponse(skuRepository.save(sku));
    }

    public SkuResponse getById(Long id) {
        ProductSku sku = skuRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("SKU", id));
        return toResponse(sku);
    }

    public PageResponse<SkuResponse> page(SkuQueryRequest query) {
        Specification<ProductSku> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deletedFlag"), 0));
            if (query.getSpuId() != null) {
                predicates.add(cb.equal(root.get("spuId"), query.getSpuId()));
            }
            if (StringUtils.hasText(query.getSkuName())) {
                predicates.add(cb.like(root.get("skuName"), "%" + query.getSkuName() + "%"));
            }
            if (StringUtils.hasText(query.getSkuCode())) {
                predicates.add(cb.equal(root.get("skuCode"), query.getSkuCode()));
            }
            if (StringUtils.hasText(query.getBarcode())) {
                predicates.add(cb.equal(root.get("barcode"), query.getBarcode()));
            }
            if (query.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), query.getStatus()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize(), sort);
        Page<ProductSku> page = skuRepository.findAll(spec, pageable);
        List<SkuResponse> list = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageResponse.of(list, page.getTotalElements(), query.getPageNum(), query.getPageSize());
    }

    @Transactional
    public void changeStatus(Long id, Integer status) {
        ProductSku sku = skuRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("SKU", id));
        sku.setStatus(status);
        sku.setUpdatedBy(SecurityUtils.getCurrentUserId());
        sku.setUpdatedAt(LocalDateTime.now());
        skuRepository.save(sku);
    }

    // ---------- 私有 ----------
    private SkuResponse toResponse(ProductSku sku) {
        SkuResponse resp = BeanCopyUtils.copyBean(sku, SkuResponse.class);
        ProductSpu spu = spuRepository.findById(sku.getSpuId()).orElse(null);
        if (spu != null) {
            resp.setSpuName(spu.getSpuName());
        }
        return resp;
    }
}
