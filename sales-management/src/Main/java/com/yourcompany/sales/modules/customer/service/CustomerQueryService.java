package com.yourcompany.sales.modules.customer.service;

import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.customer.entity.Customer;
import com.yourcompany.sales.modules.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 客户查询门面服务 —— 提供给后端 C 的报价/订单模块使用。
 *
 * 解决《需求文档》4.3/4.5/4.6 中的两条业务规则：
 *  - 禁用客户后不允许新建报价与订单
 *  - 客户信用额度不足时必须提示风险（此处直接抛 BusinessException，前端接收后可弹风险提示）
 *
 * 后端 C 的 QuoteService#createQuote、OrderService#createOrder 在处理前端参数时
 * 只需调用 requireActive(customerId) 或 requireCreditAvailable(...) 即可。
 */
@Service
@RequiredArgsConstructor
public class CustomerQueryService {

    private final CustomerRepository customerRepository;

    /**
     * 校验客户存在且未禁用，否则抛业务异常。
     *
     * @param customerId 客户主键
     * @return 通过校验的客户实体（可用于复用其 name/ownerId 等字段）
     */
    @Transactional(readOnly = true)
    public Customer requireActive(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> BusinessException.notFound("客户", customerId));
        if (customer.getDeletedFlag() != null && customer.getDeletedFlag() == 1) {
            throw BusinessException.notFound("客户", customerId);
        }
        if (customer.getStatus() == null || customer.getStatus() != 1) {
            throw BusinessException.operationForbidden("客户已被禁用，无法继续创建报价/订单");
        }
        return customer;
    }

    /**
     * 仅按 ID 查询（可能返回已禁用/已删除），列表展示场景使用。
     */
    @Transactional(readOnly = true)
    public Optional<Customer> findById(Long customerId) {
        return customerRepository.findById(customerId);
    }
}
