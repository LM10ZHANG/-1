package com.salesms.product.repo;

import com.salesms.product.entity.ProductSku;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {

    Optional<ProductSku> findByIdAndDeletedFlag(Long id, Integer deletedFlag);

    boolean existsBySkuCodeAndDeletedFlag(String skuCode, Integer deletedFlag);

    @Query("""
            select s
            from ProductSku s
            where s.deletedFlag = :deletedFlag
              and (:spuId is null or s.spuId = :spuId)
              and (:status is null or s.status = :status)
              and (:keyword is null or (s.skuName like concat('%', :keyword, '%') or s.skuCode like concat('%', :keyword, '%')))
        """)
    Page<ProductSku> search(
            @Param("deletedFlag") Integer deletedFlag,
            @Param("spuId") Long spuId,
            @Param("status") Integer status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}

