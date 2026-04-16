package com.salesms.product.repo;

import com.salesms.product.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    Page<ProductCategory> findAllByDeletedFlag(Integer deletedFlag, Pageable pageable);
}

