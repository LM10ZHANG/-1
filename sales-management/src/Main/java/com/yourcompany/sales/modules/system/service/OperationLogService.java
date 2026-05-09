package com.yourcompany.sales.modules.system.service;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.modules.system.dto.OperationLogResponse;
import com.yourcompany.sales.modules.system.entity.OperationLog;
import com.yourcompany.sales.modules.system.repository.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogRepository operationLogRepository;

    public PageResponse<OperationLogResponse> page(String moduleName, String username, Integer pageNum, Integer pageSize) {
        int pn = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int ps = pageSize == null || pageSize < 1 ? 10 : pageSize;
        Specification<OperationLog> spec = (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            if (StringUtils.hasText(moduleName)) {
                predicates.add(cb.equal(root.get("moduleName"), moduleName));
            }
            if (StringUtils.hasText(username)) {
                predicates.add(cb.like(root.get("operatorUsername"), "%" + username + "%"));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
        var page = operationLogRepository.findAll(spec, PageRequest.of(pn - 1, ps, Sort.by(Sort.Direction.DESC, "id")));
        return PageResponse.of(page.getContent().stream().map(this::toResponse).toList(), page.getTotalElements(), pn, ps);
    }

    public OperationLogResponse toResponse(OperationLog log) {
        return OperationLogResponse.builder()
                .id(log.getId())
                .moduleName(log.getModuleName())
                .actionName(log.getActionName())
                .bizType(log.getBizType())
                .operatorUserId(log.getOperatorUserId())
                .operatorUsername(log.getOperatorUsername())
                .requestMethod(log.getRequestMethod())
                .requestUri(log.getRequestUri())
                .clientIp(log.getClientIp())
                .traceId(log.getTraceId())
                .successFlag(log.getSuccessFlag())
                .errorMessage(log.getErrorMessage())
                .costMs(log.getCostMs())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
