package com.yourcompany.sales.modules.order.repository;

import com.yourcompany.sales.modules.order.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<SalesOrder, Long>,
        JpaSpecificationExecutor<SalesOrder> {

    Optional<SalesOrder> findByOrderNo(String orderNo);

    boolean existsByOrderNo(String orderNo);
}