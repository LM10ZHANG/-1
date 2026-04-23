package com.yourcompany.sales.modules.customer.service;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.customer.dto.ContactResponse;
import com.yourcompany.sales.modules.customer.dto.CustomerCreateRequest;
import com.yourcompany.sales.modules.customer.dto.CustomerQueryRequest;
import com.yourcompany.sales.modules.customer.dto.CustomerResponse;
import com.yourcompany.sales.modules.customer.dto.CustomerUpdateRequest;
import com.yourcompany.sales.modules.customer.entity.Customer;
import com.yourcompany.sales.modules.customer.entity.CustomerContact;
import com.yourcompany.sales.modules.customer.repository.CustomerContactRepository;
import com.yourcompany.sales.modules.customer.repository.CustomerRepository;
import com.yourcompany.sales.utils.BeanCopyUtils;
import com.yourcompany.sales.utils.SecurityUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户业务服务
 *
 * 职责：客户 CRUD、分页查询、禁用/启用、根据权限过滤。
 * 业务规则（《需求文档》4.3 + 分工文档 4.2）：
 * 1. 客户编码必须唯一，客户名称 + 手机/邮箱用于重复校验。
 * 2. 禁用后不允许继续新建报价/订单（通过 CustomerQueryService 校验）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerContactRepository customerContactRepository;

    /**
     * 新增客户
     */
    @Transactional
    public CustomerResponse create(CustomerCreateRequest request) {
        if (customerRepository.existsByCustomerCode(request.getCustomerCode())) {
            throw BusinessException.alreadyExists("客户", "编码", request.getCustomerCode());
        }
        if (customerRepository.existsByCustomerNameAndDeletedFlag(request.getCustomerName(), 0)) {
            throw BusinessException.alreadyExists("客户", "名称", request.getCustomerName());
        }

        Customer customer = new Customer();
        BeanUtils.copyProperties(request, customer);
        customer.setStatus(1);
        customer.setCreatedBy(SecurityUtils.getCurrentUserId());
        customer.setCreatedAt(LocalDateTime.now());

        Customer saved = customerRepository.save(customer);
        log.info("新增客户成功, id={}, code={}", saved.getId(), saved.getCustomerCode());
        return toResponse(saved, false);
    }

    /**
     * 更新客户基本信息
     */
    @Transactional
    public CustomerResponse update(Long id, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("客户", id));

        BeanUtils.copyProperties(request, customer);
        customer.setUpdatedBy(SecurityUtils.getCurrentUserId());
        customer.setUpdatedAt(LocalDateTime.now());

        Customer saved = customerRepository.save(customer);
        log.info("更新客户成功, id={}", id);
        return toResponse(saved, false);
    }

    /**
     * 查询客户详情（含联系人列表）
     */
    public CustomerResponse getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("客户", id));
        if (customer.getDeletedFlag() != null && customer.getDeletedFlag() == 1) {
            throw BusinessException.notFound("客户", id);
        }
        return toResponse(customer, true);
    }

    /**
     * 分页查询客户列表
     */
    public PageResponse<CustomerResponse> page(CustomerQueryRequest query) {
        Specification<Customer> spec = buildSpec(query);

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize(), sort);

        Page<Customer> page = customerRepository.findAll(spec, pageable);
        List<CustomerResponse> list = page.getContent().stream()
                .map(c -> toResponse(c, false))
                .collect(Collectors.toList());

        return PageResponse.of(list, page.getTotalElements(), query.getPageNum(), query.getPageSize());
    }

    /**
     * 启用/禁用客户
     * 业务规则：禁用客户后不允许再新建报价/订单（由 CustomerQueryService 在报价/订单创建时校验）。
     */
    @Transactional
    public void changeStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值仅允许 0 或 1");
        }
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("客户", id));
        customer.setStatus(status);
        customer.setUpdatedBy(SecurityUtils.getCurrentUserId());
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
        log.info("客户状态变更, id={}, status={}", id, status);
    }

    /**
     * 逻辑删除客户
     */
    @Transactional
    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("客户", id));
        customer.setDeletedFlag(1);
        customer.setUpdatedBy(SecurityUtils.getCurrentUserId());
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
        log.info("逻辑删除客户, id={}", id);
    }

    // ---------- 内部方法 ----------

    private Specification<Customer> buildSpec(CustomerQueryRequest query) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deletedFlag"), 0));

            if (StringUtils.hasText(query.getCustomerName())) {
                predicates.add(cb.like(root.get("customerName"), "%" + query.getCustomerName() + "%"));
            }
            if (StringUtils.hasText(query.getCustomerLevel())) {
                predicates.add(cb.equal(root.get("customerLevel"), query.getCustomerLevel()));
            }
            if (StringUtils.hasText(query.getCustomerType())) {
                predicates.add(cb.equal(root.get("customerType"), query.getCustomerType()));
            }
            if (StringUtils.hasText(query.getIndustry())) {
                predicates.add(cb.equal(root.get("industry"), query.getIndustry()));
            }
            if (query.getOwnerUserId() != null) {
                predicates.add(cb.equal(root.get("ownerUserId"), query.getOwnerUserId()));
            }
            if (query.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), query.getStatus()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private CustomerResponse toResponse(Customer customer, boolean withContacts) {
        CustomerResponse resp = BeanCopyUtils.copyBean(customer, CustomerResponse.class);
        if (withContacts) {
            List<CustomerContact> contacts =
                    customerContactRepository.findByCustomerIdAndDeletedFlag(customer.getId(), 0);
            List<ContactResponse> contactResps = contacts.stream()
                    .map(c -> BeanCopyUtils.copyBean(c, ContactResponse.class))
                    .collect(Collectors.toList());
            resp.setContacts(contactResps);
        }
        return resp;
    }

}
