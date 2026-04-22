package com.yourcompany.sales.modules.quote.repository;

import com.yourcompany.sales.modules.quote.entity.SalesQuoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 报价单明细数据访问接口
 */
@Repository
public interface QuoteItemRepository extends JpaRepository<SalesQuoteItem, Long> {

    /**
     * 根据报价单ID查询所有明细
     */
    List<SalesQuoteItem> findByQuoteId(Long quoteId);

    /**
     * 根据报价单ID删除所有明细
     */
    void deleteByQuoteId(Long quoteId);
}