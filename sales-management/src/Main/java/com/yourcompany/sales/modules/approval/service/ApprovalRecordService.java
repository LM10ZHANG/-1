package com.yourcompany.sales.modules.approval.service;

import com.yourcompany.sales.common.enums.BizType;
import com.yourcompany.sales.modules.approval.entity.ApprovalRecord;
import com.yourcompany.sales.modules.approval.repository.ApprovalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 审批记录服务
 */
@Service
@RequiredArgsConstructor
public class ApprovalRecordService {

    private final ApprovalRecordRepository approvalRecordRepository;

    /**
     * 保存审批记录
     */
    public ApprovalRecord save(ApprovalRecord record) {
        return approvalRecordRepository.save(record);
    }

    /**
     * 查询某业务单据的审批历史
     */
    public List<ApprovalRecord> getHistory(BizType bizType, Long bizId) {
        return approvalRecordRepository.findByBizTypeAndBizIdOrderByActionTimeDesc(bizType, bizId);
    }

    /**
     * 根据业务单号查询审批历史
     */
    public List<ApprovalRecord> getHistoryByBizNo(String bizNo) {
        return approvalRecordRepository.findByBizNoOrderByActionTimeDesc(bizNo);
    }
}