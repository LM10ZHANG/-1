package com.yourcompany.sales.modules.customer.controller;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.modules.customer.dto.ContactRequest;
import com.yourcompany.sales.modules.customer.dto.ContactResponse;
import com.yourcompany.sales.modules.customer.dto.CustomerCreateRequest;
import com.yourcompany.sales.modules.customer.dto.CustomerQueryRequest;
import com.yourcompany.sales.modules.customer.dto.CustomerResponse;
import com.yourcompany.sales.modules.customer.dto.CustomerUpdateRequest;
import com.yourcompany.sales.modules.customer.dto.FollowupRequest;
import com.yourcompany.sales.modules.customer.dto.FollowupResponse;
import com.yourcompany.sales.modules.customer.service.CustomerContactService;
import com.yourcompany.sales.modules.customer.service.CustomerFollowupService;
import com.yourcompany.sales.modules.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户中心控制器（对应《需求文档》4.3 + 分工文档 4.2 的 API 列表）
 *
 * 路由规划：
 *  - GET    /api/customers                                分页查询
 *  - POST   /api/customers                                新增客户
 *  - PUT    /api/customers/{id}                           更新客户
 *  - GET    /api/customers/{id}                           查询详情（含联系人）
 *  - PUT    /api/customers/{id}/status                    启用/禁用
 *  - DELETE /api/customers/{id}                           逻辑删除
 *  - GET    /api/customers/{id}/contacts                  联系人列表
 *  - POST   /api/customers/{id}/contacts                  新增联系人
 *  - PUT    /api/customers/{id}/contacts/{cid}            更新联系人
 *  - DELETE /api/customers/{id}/contacts/{cid}            删除联系人
 *  - GET    /api/customers/{id}/followups                 跟进记录分页
 *  - POST   /api/customers/{id}/followups                 新增跟进记录
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerContactService customerContactService;
    private final CustomerFollowupService customerFollowupService;

    // ========== 客户主体 ==========

    @PostMapping
    public ApiResponse<CustomerResponse> create(@Valid @RequestBody CustomerCreateRequest request) {
        return ApiResponse.success(customerService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<CustomerResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody CustomerUpdateRequest request) {
        return ApiResponse.success(customerService.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(customerService.getById(id));
    }

    @GetMapping
    public ApiResponse<PageResponse<CustomerResponse>> page(CustomerQueryRequest query) {
        return ApiResponse.success(customerService.page(query));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        customerService.changeStatus(id, status);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ApiResponse.success();
    }

    // ========== 联系人 ==========

    @GetMapping("/{id}/contacts")
    public ApiResponse<List<ContactResponse>> listContacts(@PathVariable("id") Long customerId) {
        return ApiResponse.success(customerContactService.listByCustomer(customerId));
    }

    @PostMapping("/{id}/contacts")
    public ApiResponse<ContactResponse> createContact(@PathVariable("id") Long customerId,
                                                      @Valid @RequestBody ContactRequest request) {
        return ApiResponse.success(customerContactService.create(customerId, request));
    }

    @PutMapping("/{id}/contacts/{cid}")
    public ApiResponse<ContactResponse> updateContact(@PathVariable("id") Long customerId,
                                                      @PathVariable("cid") Long contactId,
                                                      @Valid @RequestBody ContactRequest request) {
        return ApiResponse.success(customerContactService.update(customerId, contactId, request));
    }

    @DeleteMapping("/{id}/contacts/{cid}")
    public ApiResponse<Void> deleteContact(@PathVariable("id") Long customerId,
                                           @PathVariable("cid") Long contactId) {
        customerContactService.delete(customerId, contactId);
        return ApiResponse.success();
    }

    // ========== 跟进记录 ==========

    @PostMapping("/{id}/followups")
    public ApiResponse<FollowupResponse> createFollowup(@PathVariable("id") Long customerId,
                                                        @Valid @RequestBody FollowupRequest request) {
        return ApiResponse.success(customerFollowupService.create(customerId, request));
    }

    @GetMapping("/{id}/followups")
    public ApiResponse<PageResponse<FollowupResponse>> pageFollowups(@PathVariable("id") Long customerId,
                                                                     @RequestParam(defaultValue = "1") Integer pageNum,
                                                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(customerFollowupService.pageByCustomer(customerId, pageNum, pageSize));
    }
}
