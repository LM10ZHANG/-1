package com.salesms.customer.repo;

import com.salesms.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByIdAndDeletedFlag(Long id, Integer deletedFlag);

    Optional<Customer> findByCustomerCodeAndDeletedFlag(String customerCode, Integer deletedFlag);

    boolean existsByCustomerCodeAndDeletedFlag(String customerCode, Integer deletedFlag);

    Page<Customer> findAllByDeletedFlag(Integer deletedFlag, Pageable pageable);

    @Query("""
            select c
            from Customer c
            where c.deletedFlag = :deletedFlag
              and (:ownerUserId is null or c.ownerUserId = :ownerUserId)
              and (:keyword is null or c.customerName like concat('%', :keyword, '%'))
        """)
    Page<Customer> search(
            @Param("deletedFlag") Integer deletedFlag,
            @Param("ownerUserId") Long ownerUserId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}

