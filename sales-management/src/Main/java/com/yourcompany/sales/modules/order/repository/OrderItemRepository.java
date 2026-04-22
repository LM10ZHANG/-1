package com.yourcompany.sales.modules.order.repository;

import com.yourcompany.sales.modules.order.entity.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<SalesOrderItem, Long> {

    List<SalesOrderItem> findByOrderId(Long orderId);

    void deleteByOrderId(Long orderId);
}