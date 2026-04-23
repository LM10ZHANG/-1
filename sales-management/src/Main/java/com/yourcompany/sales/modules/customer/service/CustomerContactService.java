package com.yourcompany.sales.modules.customer.service;

import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.customer.dto.ContactRequest;
import com.yourcompany.sales.modules.customer.dto.ContactResponse;
import com.yourcompany.sales.modules.customer.entity.Customer;
import com.yourcompany.sales.modules.customer.entity.CustomerContact;
import com.yourcompany.sales.modules.customer.repository.CustomerContactRepository;
import com.yourcompany.sales.modules.customer.repository.CustomerRepository;
import com.yourcompany.sales.utils.BeanCopyUtils;
import com.yourcompany.sales.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户联系人业务服务
 *
 * 业务规则（《需求文档》4.3）：
 * 1. 主联系人最多只能有一个，新增或设置主联系人时其它自动置为非主。
 * 2. 联系人必须挂在已存在且未被逻辑删除的客户下。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerContactService {

    private final CustomerRepository customerRepository;
    private final CustomerContactRepository customerContactRepository;

    /**
     * 新增联系人
     */
    @Transactional
    public ContactResponse create(Long customerId, ContactRequest request) {
        ensureCustomerExists(customerId);

        CustomerContact contact = new CustomerContact();
        BeanUtils.copyProperties(request, contact);
        contact.setCustomerId(customerId);
        if (contact.getIsPrimary() == null) {
            contact.setIsPrimary(0);
        }
        contact.setCreatedBy(SecurityUtils.getCurrentUserId());
        contact.setCreatedAt(LocalDateTime.now());

        CustomerContact saved = customerContactRepository.save(contact);

        // 如果新增为主联系人，把其它主联系人清掉
        if (saved.getIsPrimary() != null && saved.getIsPrimary() == 1) {
            customerContactRepository.clearPrimaryExcept(customerId, saved.getId());
        }

        log.info("新增联系人成功, customerId={}, contactId={}", customerId, saved.getId());
        return BeanCopyUtils.copyBean(saved, ContactResponse.class);
    }

    /**
     * 更新联系人
     */
    @Transactional
    public ContactResponse update(Long customerId, Long contactId, ContactRequest request) {
        CustomerContact contact = customerContactRepository.findById(contactId)
                .orElseThrow(() -> BusinessException.notFound("联系人", contactId));
        if (!contact.getCustomerId().equals(customerId)) {
            throw new BusinessException("联系人不属于该客户");
        }

        BeanUtils.copyProperties(request, contact);
        contact.setCustomerId(customerId);
        contact.setUpdatedBy(SecurityUtils.getCurrentUserId());
        contact.setUpdatedAt(LocalDateTime.now());

        CustomerContact saved = customerContactRepository.save(contact);

        if (saved.getIsPrimary() != null && saved.getIsPrimary() == 1) {
            customerContactRepository.clearPrimaryExcept(customerId, saved.getId());
        }

        return BeanCopyUtils.copyBean(saved, ContactResponse.class);
    }

    /**
     * 查询客户的联系人列表
     */
    public List<ContactResponse> listByCustomer(Long customerId) {
        ensureCustomerExists(customerId);
        return customerContactRepository.findByCustomerIdAndDeletedFlag(customerId, 0).stream()
                .map(c -> BeanCopyUtils.copyBean(c, ContactResponse.class))
                .collect(Collectors.toList());
    }

    /**
     * 逻辑删除联系人
     */
    @Transactional
    public void delete(Long customerId, Long contactId) {
        CustomerContact contact = customerContactRepository.findById(contactId)
                .orElseThrow(() -> BusinessException.notFound("联系人", contactId));
        if (!contact.getCustomerId().equals(customerId)) {
            throw new BusinessException("联系人不属于该客户");
        }
        contact.setDeletedFlag(1);
        contact.setUpdatedBy(SecurityUtils.getCurrentUserId());
        contact.setUpdatedAt(LocalDateTime.now());
        customerContactRepository.save(contact);
    }

    // ---------- 私有方法 ----------
    private void ensureCustomerExists(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> BusinessException.notFound("客户", customerId));
        if (customer.getDeletedFlag() != null && customer.getDeletedFlag() == 1) {
            throw BusinessException.notFound("客户", customerId);
        }
    }
}
