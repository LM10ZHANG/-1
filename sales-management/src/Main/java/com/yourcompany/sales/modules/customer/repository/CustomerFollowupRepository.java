package com.yourcompany.sales.modules.customer.repository;

import com.yourcompany.sales.modules.customer.entity.CustomerFollowup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 客户跟进记录数据访问接口
 */
@Repository
public interface CustomerFollowupRepository extends JpaRepository<CustomerFollowup, Long> {

    Page<CustomerFollowup> findByCustomerIdAndDeletedFlagOrderByCreatedAtDesc(
            Long customerId, Integer deletedFlag, Pageable pageable);
}
