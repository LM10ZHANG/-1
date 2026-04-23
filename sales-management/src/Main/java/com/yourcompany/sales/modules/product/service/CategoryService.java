package com.yourcompany.sales.modules.product.service;

import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.product.dto.CategoryNode;
import com.yourcompany.sales.modules.product.dto.CategoryRequest;
import com.yourcompany.sales.modules.product.entity.ProductCategory;
import com.yourcompany.sales.modules.product.repository.ProductCategoryRepository;
import com.yourcompany.sales.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品分类服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProductCategoryRepository categoryRepository;

    /**
     * 新增分类
     */
    @Transactional
    public ProductCategory create(CategoryRequest request) {
        if (categoryRepository.existsByCategoryCode(request.getCategoryCode())) {
            throw BusinessException.alreadyExists("分类", "编码", request.getCategoryCode());
        }
        ProductCategory category = new ProductCategory();
        BeanUtils.copyProperties(request, category);
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        category.setCreatedBy(SecurityUtils.getCurrentUserId());
        category.setCreatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }

    /**
     * 更新分类
     */
    @Transactional
    public ProductCategory update(Long id, CategoryRequest request) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("分类", id));
        category.setCategoryName(request.getCategoryName());
        category.setParentId(request.getParentId() == null ? 0L : request.getParentId());
        category.setSortNo(request.getSortNo() == null ? 0 : request.getSortNo());
        category.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        category.setRemark(request.getRemark());
        category.setUpdatedBy(SecurityUtils.getCurrentUserId());
        category.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }

    /**
     * 查询完整分类树
     */
    public List<CategoryNode> tree() {
        List<ProductCategory> all = categoryRepository.findByDeletedFlagOrderBySortNoAsc(0);
        Map<Long, CategoryNode> idToNode = new HashMap<>();
        for (ProductCategory c : all) {
            idToNode.put(c.getId(), CategoryNode.builder()
                    .id(c.getId())
                    .parentId(c.getParentId())
                    .categoryCode(c.getCategoryCode())
                    .categoryName(c.getCategoryName())
                    .sortNo(c.getSortNo())
                    .status(c.getStatus())
                    .remark(c.getRemark())
                    .children(new ArrayList<>())
                    .build());
        }
        List<CategoryNode> roots = new ArrayList<>();
        for (CategoryNode node : idToNode.values()) {
            if (node.getParentId() == null || node.getParentId() == 0L) {
                roots.add(node);
            } else {
                CategoryNode parent = idToNode.get(node.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                } else {
                    // 找不到父节点时挂到根
                    roots.add(node);
                }
            }
        }
        return roots;
    }

    /**
     * 查询单个分类（供 SPU 详情时用）
     */
    public ProductCategory findByIdOrNull(Long id) {
        if (id == null) return null;
        return categoryRepository.findById(id).orElse(null);
    }
}
