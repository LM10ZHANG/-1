package com.yourcompany.sales.modules.product.service;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.product.dto.SpuQueryRequest;
import com.yourcompany.sales.modules.product.dto.SpuRequest;
import com.yourcompany.sales.modules.product.dto.SpuResponse;
import com.yourcompany.sales.modules.product.entity.ProductCategory;
import com.yourcompany.sales.modules.product.entity.ProductSpu;
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
 * SPU 业务服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpuService {

    private final ProductSpuRepository spuRepository;
    private final CategoryService categoryService;

    @Transactional
    public SpuResponse create(SpuRequest request) {
        if (spuRepository.existsBySpuCode(request.getSpuCode())) {
            throw BusinessException.alreadyExists("SPU", "编码", request.getSpuCode());
        }
        ProductSpu spu = new ProductSpu();
        BeanUtils.copyProperties(request, spu);
        if (spu.getStatus() == null) spu.setStatus(1);
        spu.setCreatedBy(SecurityUtils.getCurrentUserId());
        spu.setCreatedAt(LocalDateTime.now());
        ProductSpu saved = spuRepository.save(spu);
        log.info("新增 SPU 成功, id={}, code={}", saved.getId(), saved.getSpuCode());
        return toResponse(saved);
    }

    @Transactional
    public SpuResponse update(Long id, SpuRequest request) {
        ProductSpu spu = spuRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("SPU", id));
        spu.setSpuName(request.getSpuName());
        spu.setCategoryId(request.getCategoryId());
        spu.setBrandName(request.getBrandName());
        spu.setUnitName(request.getUnitName());
        spu.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            spu.setStatus(request.getStatus());
        }
        spu.setUpdatedBy(SecurityUtils.getCurrentUserId());
        spu.setUpdatedAt(LocalDateTime.now());
        return toResponse(spuRepository.save(spu));
    }

    public SpuResponse getById(Long id) {
        ProductSpu spu = spuRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("SPU", id));
        return toResponse(spu);
    }

    public PageResponse<SpuResponse> page(SpuQueryRequest query) {
        Specification<ProductSpu> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deletedFlag"), 0));
            if (StringUtils.hasText(query.getSpuName())) {
                predicates.add(cb.like(root.get("spuName"), "%" + query.getSpuName() + "%"));
            }
            if (query.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), query.getCategoryId()));
            }
            if (query.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), query.getStatus()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize(), sort);
        Page<ProductSpu> page = spuRepository.findAll(spec, pageable);
        List<SpuResponse> list = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageResponse.of(list, page.getTotalElements(), query.getPageNum(), query.getPageSize());
    }

    @Transactional
    public void changeStatus(Long id, Integer status) {
        ProductSpu spu = spuRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("SPU", id));
        spu.setStatus(status);
        spu.setUpdatedBy(SecurityUtils.getCurrentUserId());
        spu.setUpdatedAt(LocalDateTime.now());
        spuRepository.save(spu);
    }

    // ---------- 私有 ----------
    private SpuResponse toResponse(ProductSpu spu) {
        SpuResponse resp = BeanCopyUtils.copyBean(spu, SpuResponse.class);
        if (spu.getCategoryId() != null) {
            ProductCategory cat = categoryService.findByIdOrNull(spu.getCategoryId());
            if (cat != null) {
                resp.setCategoryName(cat.getCategoryName());
            }
        }
        return resp;
    }
}
