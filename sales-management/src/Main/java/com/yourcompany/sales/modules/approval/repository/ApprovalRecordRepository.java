package com.yourcompany.sales.modules.approval.repository;

import com.yourcompany.sales.common.enums.BizType;
import com.yourcompany.sales.modules.approval.entity.ApprovalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRecordRepository extends JpaRepository<ApprovalRecord, Long>,
        JpaSpecificationExecutor<ApprovalRecord> {

    /**
     * 根据业务类型和业务ID查询审批记录
     */
    List<ApprovalRecord> findByBizTypeAndBizIdOrderByActionTimeDesc(BizType bizType, Long bizId);

    /**
     * 根据业务单号查询
     */
    List<ApprovalRecord> findByBizNoOrderByActionTimeDesc(String bizNo);
}