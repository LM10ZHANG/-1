package com.salesms.customer.controller;

import com.salesms.common.ApiResponse;
import com.salesms.common.PageResult;
import com.salesms.common.TraceIdContext;
import com.salesms.common.UserContext;
import com.salesms.customer.dto.*;
import com.salesms.customer.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final UserContext userContext;

    public CustomerController(CustomerService customerService, UserContext userContext) {
        this.customerService = customerService;
        this.userContext = userContext;
    }

    @PostMapping
    public ApiResponse<Long> createCustomer(
            @Valid @RequestBody CustomerCreateRequest req,
            HttpServletRequest request
    ) {
        Long operatorUserId = userContext.getUserId(request);
        Long id = customerService.createCustomer(req, operatorUserId);
        return ApiResponse.success(TraceIdContext.get(), id);
    }

    @GetMapping
    public ApiResponse<PageResult<CustomerSummaryResponse>> listCustomers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "ownerUserId", required = false) Long ownerUserId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        PageResult<CustomerSummaryResponse> result = customerService.listCustomers(keyword, ownerUserId, page, size);
        return ApiResponse.success(TraceIdContext.get(), result);
    }

    @PutMapping("/{id}")
    public ApiResponse<Long> updateCustomer(
            @PathVariable("id") Long id,
            @Valid @RequestBody CustomerUpdateRequest req,
            HttpServletRequest request
    ) {
        Long operatorUserId = userContext.getUserId(request);
        Long customerId = customerService.updateCustomer(id, req, operatorUserId);
        return ApiResponse.success(TraceIdContext.get(), customerId);
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerDetailResponse> getCustomerDetail(@PathVariable("id") Long id) {
        CustomerDetailResponse resp = customerService.getCustomerDetail(id);
        return ApiResponse.success(TraceIdContext.get(), resp);
    }

    @PostMapping("/{id}/contacts")
    public ApiResponse<Long> addContact(
            @PathVariable("id") Long id,
            @Valid @RequestBody CustomerContactCreateRequest req,
            HttpServletRequest request
    ) {
        Long operatorUserId = userContext.getUserId(request);
        Long contactId = customerService.addContact(id, req, operatorUserId);
        return ApiResponse.success(TraceIdContext.get(), contactId);
    }

    @PostMapping("/{id}/followups")
    public ApiResponse<Long> addFollowup(
            @PathVariable("id") Long id,
            @Valid @RequestBody CustomerFollowupCreateRequest req,
            HttpServletRequest request
    ) {
        Long operatorUserId = userContext.getUserId(request);
        Long followupId = customerService.addFollowup(id, req, operatorUserId);
        return ApiResponse.success(TraceIdContext.get(), followupId);
    }
}

