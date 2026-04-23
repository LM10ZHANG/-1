package com.yourcompany.sales.modules.customer.repository;

import com.yourcompany.sales.modules.customer.entity.CustomerContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 客户联系人数据访问接口
 */
@Repository
public interface CustomerContactRepository extends JpaRepository<CustomerContact, Long> {

    List<CustomerContact> findByCustomerIdAndDeletedFlag(Long customerId, Integer deletedFlag);

    boolean existsByCustomerIdAndIsPrimaryAndDeletedFlag(Long customerId, Integer isPrimary, Integer deletedFlag);

    /**
     * 把指定客户下除 keepId 以外的联系人全部置为非主（用于保证"主联系人最多一个"）
     */
    @Modifying
    @Query("UPDATE CustomerContact c SET c.isPrimary = 0 " +
            "WHERE c.customerId = :customerId AND c.id <> :keepId AND c.deletedFlag = 0 AND c.isPrimary = 1")
    int clearPrimaryExcept(@Param("customerId") Long customerId, @Param("keepId") Long keepId);
}
