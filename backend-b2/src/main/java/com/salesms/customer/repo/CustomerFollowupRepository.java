package com.salesms.customer.repo;

import com.salesms.customer.entity.CustomerFollowup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerFollowupRepository extends JpaRepository<CustomerFollowup, Long> {

    List<CustomerFollowup> findAllByCustomerIdAndDeletedFlag(Long customerId, Integer deletedFlag);
}

