package com.yourcompany.sales.modules.customer.service;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.customer.dto.FollowupRequest;
import com.yourcompany.sales.modules.customer.dto.FollowupResponse;
import com.yourcompany.sales.modules.customer.entity.Customer;
import com.yourcompany.sales.modules.customer.entity.CustomerFollowup;
import com.yourcompany.sales.modules.customer.repository.CustomerFollowupRepository;
import com.yourcompany.sales.modules.customer.repository.CustomerRepository;
import com.yourcompany.sales.utils.BeanCopyUtils;
import com.yourcompany.sales.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户跟进记录业务服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerFollowupService {

    private final CustomerRepository customerRepository;
    private final CustomerFollowupRepository customerFollowupRepository;

    /**
     * 新增一条跟进记录
     */
    @Transactional
    public FollowupResponse create(Long customerId, FollowupRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> BusinessException.notFound("客户", customerId));
        if (customer.getDeletedFlag() != null && customer.getDeletedFlag() == 1) {
            throw BusinessException.notFound("客户", customerId);
        }

        CustomerFollowup followup = new CustomerFollowup();
        BeanUtils.copyProperties(request, followup);
        followup.setCustomerId(customerId);
        followup.setFollowUserId(SecurityUtils.getCurrentUserId());
        followup.setCreatedBy(SecurityUtils.getCurrentUserId());
        followup.setCreatedAt(LocalDateTime.now());

        CustomerFollowup saved = customerFollowupRepository.save(followup);

        // 同步更新客户的跟进状态（简化：以跟进结果作为当前状态）
        if (request.getFollowResult() != null) {
            customer.setFollowStatus(request.getFollowResult());
            customer.setUpdatedAt(LocalDateTime.now());
            customerRepository.save(customer);
        }

        log.info("新增跟进记录成功, customerId={}, followupId={}", customerId, saved.getId());
        return BeanCopyUtils.copyBean(saved, FollowupResponse.class);
    }

    /**
     * 分页查询某客户的跟进记录
     */
    public PageResponse<FollowupResponse> pageByCustomer(Long customerId, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<CustomerFollowup> page =
                customerFollowupRepository.findByCustomerIdAndDeletedFlagOrderByCreatedAtDesc(customerId, 0, pageable);
        List<FollowupResponse> list = page.getContent().stream()
                .map(f -> BeanCopyUtils.copyBean(f, FollowupResponse.class))
                .collect(Collectors.toList());
        return PageResponse.of(list, page.getTotalElements(), pageNum, pageSize);
    }
}
