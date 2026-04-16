package com.salesms.customer.repo;

import com.salesms.customer.entity.CustomerContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerContactRepository extends JpaRepository<CustomerContact, Long> {

    List<CustomerContact> findAllByCustomerIdAndDeletedFlag(Long customerId, Integer deletedFlag);

    @Query("""
            select (count(ct) > 0)
            from CustomerContact ct
            join ct.customer c
            where c.customerName = :customerName
              and c.deletedFlag = 0
              and ct.deletedFlag = 0
              and ct.mobile = :mobile
              and c.id <> :excludeCustomerId
        """)
    boolean existsDuplicateByCustomerNameAndMobile(
            @Param("customerName") String customerName,
            @Param("mobile") String mobile,
            @Param("excludeCustomerId") Long excludeCustomerId
    );

    @Query("""
            select (count(ct) > 0)
            from CustomerContact ct
            join ct.customer c
            where c.customerName = :customerName
              and c.deletedFlag = 0
              and ct.deletedFlag = 0
              and ct.email = :email
              and c.id <> :excludeCustomerId
        """)
    boolean existsDuplicateByCustomerNameAndEmail(
            @Param("customerName") String customerName,
            @Param("email") String email,
            @Param("excludeCustomerId") Long excludeCustomerId
    );

    @Modifying
    @Transactional
    @Query("""
            update CustomerContact ct
            set ct.isPrimary = 0
            where ct.customer.id = :customerId
              and ct.isPrimary = 1
              and ct.deletedFlag = 0
        """)
    int clearPrimaryForCustomer(@Param("customerId") Long customerId);
}

