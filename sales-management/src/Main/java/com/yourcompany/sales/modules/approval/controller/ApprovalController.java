package com.yourcompany.sales.modules.approval.controller;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.common.enums.BizType;
import com.yourcompany.sales.modules.approval.entity.ApprovalRecord;
import com.yourcompany.sales.modules.approval.service.ApprovalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审批中心控制器
 */
@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalRecordService approvalRecordService;

    /**
     * 查询审批历史（按业务类型和ID）
     */
    @GetMapping("/history")
    public ApiResponse<List<ApprovalRecord>> getHistory(@RequestParam BizType bizType,
                                                        @RequestParam Long bizId) {
        return ApiResponse.success(approvalRecordService.getHistory(bizType, bizId));
    }

    /**
     * 查询审批历史（按业务单号）
     */
    @GetMapping("/history/{bizNo}")
    public ApiResponse<List<ApprovalRecord>> getHistoryByBizNo(@PathVariable String bizNo) {
        return ApiResponse.success(approvalRecordService.getHistoryByBizNo(bizNo));
    }
}