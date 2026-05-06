package com.yourcompany.sales.modules.quote.repository;

import com.yourcompany.sales.modules.quote.entity.SalesQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 报价单主表数据访问接口
 */
@Repository
public interface QuoteRepository extends JpaRepository<SalesQuote, Long>,
        JpaSpecificationExecutor<SalesQuote> {

    /**
     * 根据报价单号查询
     */
    Optional<SalesQuote> findByQuoteNo(String quoteNo);

    /**
     * 判断报价单号是否存在
     */
    boolean existsByQuoteNo(String quoteNo);

    @Query("""
            select count(q)
            from SalesQuote q
            where q.deletedFlag = 0
              and q.status = com.yourcompany.sales.common.enums.QuoteStatus.PENDING_APPROVAL
            """)
    Long countPendingApprovalQuotes();
}
