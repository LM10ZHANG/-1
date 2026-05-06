package com.yourcompany.sales.modules.customer.repository;

import com.yourcompany.sales.modules.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 客户数据访问接口
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>,
        JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByCustomerCode(String customerCode);

    boolean existsByCustomerCode(String customerCode);

    boolean existsByCustomerNameAndDeletedFlag(String customerName, Integer deletedFlag);

    @Query("""
            select count(c)
            from Customer c
            where c.deletedFlag = 0
              and c.createdAt between :start and :end
            """)
    Long countNewCustomers(@Param("start") LocalDateTime start,
                           @Param("end") LocalDateTime end);

    @Query("""
            select c
            from Customer c
            where c.deletedFlag = 0
            order by c.createdAt desc
            """)
    List<Customer> findCustomersForWarning();
}
