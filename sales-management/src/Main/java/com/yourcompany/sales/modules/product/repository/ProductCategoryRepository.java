package com.yourcompany.sales.modules.product.repository;

import com.yourcompany.sales.modules.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品分类数据访问接口
 */
@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    boolean existsByCategoryCode(String categoryCode);

    List<ProductCategory> findByDeletedFlagOrderBySortNoAsc(Integer deletedFlag);
}
