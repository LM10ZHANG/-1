package com.yourcompany.sales.modules.quote.service;

import com.yourcompany.sales.common.enums.ApprovalAction;
import com.yourcompany.sales.common.enums.ApprovalStatus;
import com.yourcompany.sales.common.enums.BizType;
import com.yourcompany.sales.common.enums.QuoteStatus;
import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.approval.entity.ApprovalRecord;
import com.yourcompany.sales.modules.approval.repository.ApprovalRecordRepository;
import com.yourcompany.sales.modules.quote.dto.ApprovalRequest;
import com.yourcompany.sales.modules.quote.entity.SalesQuote;
import com.yourcompany.sales.modules.quote.repository.QuoteRepository;
import com.yourcompany.sales.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 报价单审批服务（可独立拆分，也可合并到 QuoteService）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuoteApprovalService {

    private final QuoteRepository quoteRepository;
    private final ApprovalRecordRepository approvalRecordRepository;

    /**
     * 审批通过
     */
    @Transactional
    public void approve(Long quoteId, ApprovalRequest request) {
        SalesQuote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> BusinessException.notFound("报价单", quoteId));

        if (quote.getStatus() != QuoteStatus.PENDING_APPROVAL) {
            throw BusinessException.invalidStatus(quote.getStatus().getDescription(), "待审批状态");
        }

        quote.setStatus(QuoteStatus.APPROVED);
        quote.setApprovalStatus(ApprovalStatus.APPROVED);
        quoteRepository.save(quote);

        saveApprovalRecord(quote, ApprovalAction.APPROVE, request.getComment());
        log.info("报价单审批通过, quoteId: {}", quoteId);
    }

    /**
     * 审批驳回
     */
    @Transactional
    public void reject(Long quoteId, ApprovalRequest request) {
        SalesQuote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> BusinessException.notFound("报价单", quoteId));

        if (quote.getStatus() != QuoteStatus.PENDING_APPROVAL) {
            throw BusinessException.invalidStatus(quote.getStatus().getDescription(), "待审批状态");
        }

        quote.setStatus(QuoteStatus.REJECTED);
        quote.setApprovalStatus(ApprovalStatus.REJECTED);
        quoteRepository.save(quote);

        saveApprovalRecord(quote, ApprovalAction.REJECT, request.getComment());
        log.info("报价单审批驳回, quoteId: {}", quoteId);
    }

    private void saveApprovalRecord(SalesQuote quote, ApprovalAction action, String comment) {
        ApprovalRecord record = ApprovalRecord.builder()
                .bizType(BizType.QUOTE)
                .bizId(quote.getId())
                .bizNo(quote.getQuoteNo())
                .applyUserId(quote.getOwnerUserId())
                .approverUserId(SecurityUtils.getCurrentUserId())
                .action(action)
                .comment(comment)
                .actionTime(LocalDateTime.now())
                .statusAfterAction(quote.getStatus().name())
                .build();
        approvalRecordRepository.save(record);
    }
}