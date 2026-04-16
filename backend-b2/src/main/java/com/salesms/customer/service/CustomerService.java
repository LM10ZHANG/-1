package com.salesms.customer.service;

import com.salesms.common.BusinessException;
import com.salesms.common.PageResult;
import com.salesms.common.TraceIdContext;
import com.salesms.customer.dto.*;
import com.salesms.customer.entity.Customer;
import com.salesms.customer.entity.CustomerContact;
import com.salesms.customer.entity.CustomerFollowup;
import com.salesms.customer.repo.CustomerContactRepository;
import com.salesms.customer.repo.CustomerFollowupRepository;
import com.salesms.customer.repo.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerContactRepository customerContactRepository;
    private final CustomerFollowupRepository customerFollowupRepository;

    public CustomerService(
            CustomerRepository customerRepository,
            CustomerContactRepository customerContactRepository,
            CustomerFollowupRepository customerFollowupRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerContactRepository = customerContactRepository;
        this.customerFollowupRepository = customerFollowupRepository;
    }

    @Transactional
    public Long createCustomer(CustomerCreateRequest req, Long operatorUserId) {
        Integer deletedFlag = 0;
        if (customerRepository.existsByCustomerCodeAndDeletedFlag(req.getCustomerCode(), deletedFlag)) {
            throw new BusinessException("CUSTOMER_CODE_DUPLICATE", "customer_code already exists");
        }

        Customer customer = new Customer();
        customer.setCustomerCode(req.getCustomerCode().trim());
        customer.setCustomerName(req.getCustomerName().trim());
        customer.setCustomerLevel(req.getCustomerLevel());
        customer.setCustomerType(req.getCustomerType());
        customer.setIndustry(req.getIndustry());
        customer.setSource(req.getSource());
        customer.setProvince(req.getProvince());
        customer.setCity(req.getCity());
        customer.setAddress(req.getAddress());
        customer.setOwnerUserId(req.getOwnerUserId() == null ? operatorUserId : req.getOwnerUserId());
        customer.setCreditLimit(req.getCreditLimit());
        customer.setCurrentArAmount(req.getCurrentArAmount());
        customer.setFollowStatus(req.getFollowStatus());
        customer.setStatus(req.getStatus() == null ? 1 : req.getStatus());
        customer.setRemark(req.getRemark());

        customer.setDeletedFlag(0);
        customer.setCreatedBy(operatorUserId);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedBy(operatorUserId);
        customer.setUpdatedAt(LocalDateTime.now());

        Customer saved = customerRepository.save(customer);
        return saved.getId();
    }

    @Transactional
    public Long updateCustomer(Long customerId, CustomerUpdateRequest req, Long operatorUserId) {
        Integer deletedFlag = 0;
        Customer customer = customerRepository.findByIdAndDeletedFlag(customerId, deletedFlag)
                .orElseThrow(() -> new BusinessException("CUSTOMER_NOT_FOUND", "customer not found"));

        // customer_code 可能会被修改：需要保证唯一
        Customer existByCode = customerRepository.findByCustomerCodeAndDeletedFlag(req.getCustomerCode(), deletedFlag).orElse(null);
        if (existByCode != null && !existByCode.getId().equals(customerId)) {
            throw new BusinessException("CUSTOMER_CODE_DUPLICATE", "customer_code already exists");
        }

        customer.setCustomerCode(req.getCustomerCode().trim());
        customer.setCustomerName(req.getCustomerName().trim());
        customer.setCustomerLevel(req.getCustomerLevel());
        customer.setCustomerType(req.getCustomerType());
        customer.setIndustry(req.getIndustry());
        customer.setSource(req.getSource());
        customer.setProvince(req.getProvince());
        customer.setCity(req.getCity());
        customer.setAddress(req.getAddress());
        customer.setOwnerUserId(req.getOwnerUserId());
        customer.setCreditLimit(req.getCreditLimit());
        customer.setCurrentArAmount(req.getCurrentArAmount());
        customer.setFollowStatus(req.getFollowStatus());
        customer.setStatus(req.getStatus() == null ? 1 : req.getStatus());
        customer.setRemark(req.getRemark());

        customer.setUpdatedBy(operatorUserId);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
        return customer.getId();
    }

    @Transactional(readOnly = true)
    public CustomerDetailResponse getCustomerDetail(Long customerId) {
        Integer deletedFlag = 0;
        Customer customer = customerRepository.findByIdAndDeletedFlag(customerId, deletedFlag)
                .orElseThrow(() -> new BusinessException("CUSTOMER_NOT_FOUND", "customer not found"));

        List<CustomerContact> contacts = customerContactRepository.findAllByCustomerIdAndDeletedFlag(customerId, deletedFlag);
        List<CustomerFollowup> followups = customerFollowupRepository.findAllByCustomerIdAndDeletedFlag(customerId, deletedFlag);

        CustomerDetailResponse resp = new CustomerDetailResponse();
        resp.setId(customer.getId());
        resp.setCustomerCode(customer.getCustomerCode());
        resp.setCustomerName(customer.getCustomerName());
        resp.setCustomerLevel(customer.getCustomerLevel());
        resp.setCustomerType(customer.getCustomerType());
        resp.setIndustry(customer.getIndustry());
        resp.setSource(customer.getSource());
        resp.setProvince(customer.getProvince());
        resp.setCity(customer.getCity());
        resp.setAddress(customer.getAddress());
        resp.setOwnerUserId(customer.getOwnerUserId());
        resp.setCreditLimit(customer.getCreditLimit());
        resp.setCurrentArAmount(customer.getCurrentArAmount());
        resp.setFollowStatus(customer.getFollowStatus());
        resp.setStatus(customer.getStatus());
        resp.setRemark(customer.getRemark());

        for (CustomerContact c : contacts) {
            CustomerDetailResponse.Contact item = new CustomerDetailResponse.Contact();
            item.setId(c.getId());
            item.setName(c.getName());
            item.setMobile(c.getMobile());
            item.setEmail(c.getEmail());
            item.setPosition(c.getPosition());
            item.setWechat(c.getWechat());
            item.setIsPrimary(c.getIsPrimary());
            item.setRemark(c.getRemark());
            item.setCreatedAt(c.getCreatedAt());
            resp.getContacts().add(item);
        }

        for (CustomerFollowup f : followups) {
            CustomerDetailResponse.Followup item = new CustomerDetailResponse.Followup();
            item.setId(f.getId());
            item.setFollowUserId(f.getFollowUserId());
            item.setFollowType(f.getFollowType());
            item.setContent(f.getContent());
            item.setNextFollowTime(f.getNextFollowTime());
            item.setFollowResult(f.getFollowResult());
            item.setCreatedAt(f.getCreatedAt());
            resp.getFollowups().add(item);
        }

        return resp;
    }

    @Transactional(readOnly = true)
    public PageResult<CustomerSummaryResponse> listCustomers(String keyword, Long ownerUserId, int page, int size) {
        Integer deletedFlag = 0;
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> result = customerRepository.search(deletedFlag, ownerUserId, keyword, pageable);

        List<Customer> records = result.getContent();
        PageResult<CustomerSummaryResponse> out = new PageResult<>();
        out.setPage(page);
        out.setSize(size);
        out.setTotal(result.getTotalElements());

        List<CustomerSummaryResponse> list = out.getRecords();
        // PageResult 默认 records 可能为 null：这里确保初始化
        if (list == null) {
            out.setRecords(new java.util.ArrayList<>());
            list = out.getRecords();
        }

        for (Customer c : records) {
            CustomerSummaryResponse s = new CustomerSummaryResponse();
            s.setId(c.getId());
            s.setCustomerCode(c.getCustomerCode());
            s.setCustomerName(c.getCustomerName());
            s.setCustomerLevel(c.getCustomerLevel());
            s.setCustomerType(c.getCustomerType());
            s.setOwnerUserId(c.getOwnerUserId());
            s.setCreditLimit(c.getCreditLimit());
            s.setFollowStatus(c.getFollowStatus());
            s.setStatus(c.getStatus());
            list.add(s);
        }
        return out;
    }

    @Transactional
    public Long addContact(Long customerId, CustomerContactCreateRequest req, Long operatorUserId) {
        Integer deletedFlag = 0;
        Customer customer = customerRepository.findByIdAndDeletedFlag(customerId, deletedFlag)
                .orElseThrow(() -> new BusinessException("CUSTOMER_NOT_FOUND", "customer not found"));

        String mobile = req.getMobile();
        String email = req.getEmail();
        if ((mobile == null || mobile.isBlank()) && (email == null || email.isBlank())) {
            throw new BusinessException("CONTACT_VALIDATION_ERROR", "mobile or email must be provided");
        }

        if (mobile != null && !mobile.isBlank()) {
            boolean duplicated = customerContactRepository.existsDuplicateByCustomerNameAndMobile(
                    customer.getCustomerName(),
                    mobile.trim(),
                    customerId
            );
            if (duplicated) {
                throw new BusinessException("DUPLICATE_CONTACT_MOBILE", "duplicate mobile for same customer name");
            }
        }

        if (email != null && !email.isBlank()) {
            boolean duplicated = customerContactRepository.existsDuplicateByCustomerNameAndEmail(
                    customer.getCustomerName(),
                    email.trim(),
                    customerId
            );
            if (duplicated) {
                throw new BusinessException("DUPLICATE_CONTACT_EMAIL", "duplicate email for same customer name");
            }
        }

        if (req.getIsPrimary() != null && req.getIsPrimary() == 1) {
            customerContactRepository.clearPrimaryForCustomer(customerId);
        }

        CustomerContact contact = new CustomerContact();
        contact.setCustomer(customer);
        contact.setName(req.getName());
        contact.setMobile(mobile == null ? null : mobile.trim());
        contact.setEmail(email == null ? null : email.trim());
        contact.setPosition(req.getPosition());
        contact.setWechat(req.getWechat());
        contact.setIsPrimary(req.getIsPrimary() == null ? 0 : req.getIsPrimary());
        contact.setRemark(req.getRemark());

        contact.setDeletedFlag(0);
        contact.setCreatedBy(operatorUserId);
        contact.setCreatedAt(LocalDateTime.now());
        contact.setUpdatedBy(operatorUserId);
        contact.setUpdatedAt(LocalDateTime.now());

        return customerContactRepository.save(contact).getId();
    }

    @Transactional
    public Long addFollowup(Long customerId, CustomerFollowupCreateRequest req, Long operatorUserId) {
        Integer deletedFlag = 0;
        Customer customer = customerRepository.findByIdAndDeletedFlag(customerId, deletedFlag)
                .orElseThrow(() -> new BusinessException("CUSTOMER_NOT_FOUND", "customer not found"));

        if (req.getFollowUserId() == null) {
            req.setFollowUserId(operatorUserId);
        }
        if (req.getFollowType() == null || req.getFollowType().isBlank()) {
            throw new BusinessException("FOLLOWUP_VALIDATION_ERROR", "follow_type is required");
        }
        if (req.getContent() == null || req.getContent().isBlank()) {
            throw new BusinessException("FOLLOWUP_VALIDATION_ERROR", "content is required");
        }

        CustomerFollowup followup = new CustomerFollowup();
        followup.setCustomer(customer);
        followup.setFollowUserId(req.getFollowUserId());
        followup.setFollowType(req.getFollowType().trim());
        followup.setContent(req.getContent());
        followup.setNextFollowTime(req.getNextFollowTime());
        followup.setFollowResult(req.getFollowResult());

        followup.setDeletedFlag(0);
        followup.setCreatedBy(operatorUserId);
        followup.setCreatedAt(LocalDateTime.now());
        followup.setUpdatedBy(operatorUserId);
        followup.setUpdatedAt(LocalDateTime.now());

        return customerFollowupRepository.save(followup).getId();
    }
}

