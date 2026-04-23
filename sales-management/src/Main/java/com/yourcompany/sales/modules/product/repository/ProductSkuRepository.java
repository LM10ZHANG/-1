package com.yourcompany.sales.modules.product.repository;

import com.yourcompany.sales.modules.product.entity.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品 SKU 数据访问接口
 */
@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long>,
        JpaSpecificationExecutor<ProductSku> {

    boolean existsBySkuCode(String skuCode);

    List<ProductSku> findBySpuIdAndDeletedFlag(Long spuId, Integer deletedFlag);
}
