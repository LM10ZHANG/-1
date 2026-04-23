package com.yourcompany.sales.modules.product.repository;

import com.yourcompany.sales.modules.product.entity.ProductSpu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 商品 SPU 数据访问接口
 */
@Repository
public interface ProductSpuRepository extends JpaRepository<ProductSpu, Long>,
        JpaSpecificationExecutor<ProductSpu> {

    boolean existsBySpuCode(String spuCode);
}
