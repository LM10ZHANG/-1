package com.salesms.product.repo;

import com.salesms.product.entity.ProductSpu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductSpuRepository extends JpaRepository<ProductSpu, Long> {

    Optional<ProductSpu> findByIdAndDeletedFlag(Long id, Integer deletedFlag);

    boolean existsBySpuCodeAndDeletedFlag(String spuCode, Integer deletedFlag);

    @Query("""
            select p
            from ProductSpu p
            where p.deletedFlag = :deletedFlag
              and (:categoryId is null or p.categoryId = :categoryId)
              and (:status is null or p.status = :status)
              and (:keyword is null or p.spuName like concat('%', :keyword, '%'))
        """)
    Page<ProductSpu> search(
            @Param("deletedFlag") Integer deletedFlag,
            @Param("categoryId") Long categoryId,
            @Param("status") Integer status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}

