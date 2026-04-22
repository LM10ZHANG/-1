package com.yourcompany.sales.modules.quote.service;

import com.yourcompany.sales.common.enums.ApprovalAction;
import com.yourcompany.sales.common.enums.ApprovalStatus;
import com.yourcompany.sales.common.enums.BizType;
import com.yourcompany.sales.common.enums.QuoteStatus;
import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.approval.entity.ApprovalRecord;
import com.yourcompany.sales.modules.approval.repository.ApprovalRecordRepository;
import com.yourcompany.sales.modules.order.entity.SalesOrder;
import com.yourcompany.sales.modules.order.entity.SalesOrderItem;
import com.yourcompany.sales.modules.order.repository.OrderRepository;
import com.yourcompany.sales.modules.quote.dto.*;
import com.yourcompany.sales.modules.quote.entity.SalesQuote;
import com.yourcompany.sales.modules.quote.entity.SalesQuoteItem;
import com.yourcompany.sales.modules.quote.repository.QuoteItemRepository;
import com.yourcompany.sales.modules.quote.repository.QuoteRepository;
import com.yourcompany.sales.utils.BeanCopyUtils;
import com.yourcompany.sales.utils.CodeGenerator;
import com.yourcompany.sales.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 报价单核心服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuoteItemRepository quoteItemRepository;
    private final OrderRepository orderRepository;
    private final ApprovalRecordRepository approvalRecordRepository;
    private final CodeGenerator codeGenerator;

    // 如果需要调用商品服务检查 SKU，可注入 FeignClient 或 Service
    // private final ProductService productService;

    /**
     * 创建报价单（草稿状态）
     */
    @Transactional
    public QuoteResponse createQuote(QuoteCreateRequest request) {
        // 1. 校验客户是否存在且状态正常（此处省略，调用客户服务）
        // 2. 生成报价单号
        String quoteNo = codeGenerator.generateQuoteNo();

        // 3. 构建报价单主实体
        SalesQuote quote = new SalesQuote();
        quote.setQuoteNo(quoteNo);
        quote.setCustomerId(request.getCustomerId());
        quote.setContactId(request.getContactId());
        quote.setQuoteDate(LocalDate.now());
        quote.setExpireDate(request.getExpireDate());
        quote.setPaymentTerm(request.getPaymentTerm());
        quote.setDeliveryMethod(request.getDeliveryMethod());
        quote.setTaxIncludedFlag(request.getTaxIncludedFlag());
        quote.setDiscountAmount(request.getDiscountAmount());
        quote.setRemark(request.getRemark());
        quote.setStatus(QuoteStatus.DRAFT);
        quote.setApprovalStatus(ApprovalStatus.NOT_SUBMIT);
        quote.setOwnerUserId(SecurityUtils.getCurrentUserId());

        // 4. 处理明细
        List<SalesQuoteItem> items = new ArrayList<>();
        for (QuoteItemRequest itemReq : request.getItems()) {
            // 这里应调用商品服务获取 SKU 信息（价格、税率、名称等），演示中模拟数据
            // ProductSku sku = productService.getSku(itemReq.getSkuId());
            // if (!sku.getStatus()) throw new BusinessException("SKU已停用");

            SalesQuoteItem item = new SalesQuoteItem();
            item.setSkuId(itemReq.getSkuId());
            item.setSkuNameSnapshot("商品名称快照"); // 实际应从 SKU 获取
            item.setQty(itemReq.getQty());
            item.setOriginUnitPrice(new BigDecimal("100.00")); // 模拟原价
            item.setDiscountRate(itemReq.getDiscountRate());
            item.setTaxRate(new BigDecimal("13.00"));          // 模拟税率
            item.setRemark(itemReq.getRemark());
            item.calculateLineAmount();
            quote.addItem(item);
        }
        quote.calculateTotal();

        // 5. 保存
        SalesQuote savedQuote = quoteRepository.save(quote);
        log.info("创建报价单成功, quoteNo: {}", quoteNo);

        return convertToResponse(savedQuote);
    }

    /**
     * 更新报价单（草稿/驳回状态可编辑，已审批编辑后回退待审批）
     */
    @Transactional
    public QuoteResponse updateQuote(Long id, QuoteUpdateRequest request) {
        SalesQuote quote = quoteRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("报价单", id));

        // 状态校验
        if (quote.getStatus().isFinal()) {
            throw BusinessException.invalidStatus(quote.getStatus().getDescription(), "可编辑状态");
        }

        // 如果当前是已审批状态，修改后需回退到待审批
        if (quote.getStatus() == QuoteStatus.APPROVED) {
            quote.setStatus(QuoteStatus.PENDING_APPROVAL);
            quote.setApprovalStatus(ApprovalStatus.PENDING);
            log.info("已审批报价单被修改，状态回退至待审批, quoteId: {}", id);
        }

        // 更新基本信息
        quote.setCustomerId(request.getCustomerId());
        quote.setContactId(request.getContactId());
        quote.setExpireDate(request.getExpireDate());
        quote.setPaymentTerm(request.getPaymentTerm());
        quote.setDeliveryMethod(request.getDeliveryMethod());
        quote.setTaxIncludedFlag(request.getTaxIncludedFlag());
        quote.setDiscountAmount(request.getDiscountAmount());
        quote.setRemark(request.getRemark());

        // 更新明细：先删除旧明细，再添加新明细
        quoteItemRepository.deleteByQuoteId(id);
        List<SalesQuoteItem> newItems = new ArrayList<>();
        for (QuoteItemRequest itemReq : request.getItems()) {
            SalesQuoteItem item = new SalesQuoteItem();
            item.setSkuId(itemReq.getSkuId());
            item.setSkuNameSnapshot("商品名称快照");
            item.setQty(itemReq.getQty());
            item.setOriginUnitPrice(new BigDecimal("100.00"));
            item.setDiscountRate(itemReq.getDiscountRate());
            item.setTaxRate(new BigDecimal("13.00"));
            item.setRemark(itemReq.getRemark());
            item.calculateLineAmount();
            quote.addItem(item);
        }
        quote.calculateTotal();

        SalesQuote updatedQuote = quoteRepository.save(quote);
        log.info("更新报价单成功, quoteId: {}", id);

        return convertToResponse(updatedQuote);
    }

    /**
     * 提交审批（含折扣阈值判断）
     */
    @Transactional
    public void submitForApproval(Long quoteId) {
        SalesQuote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> BusinessException.notFound("报价单", quoteId));

        if (!quote.getStatus().canSubmitApproval()) {
            throw BusinessException.invalidStatus(quote.getStatus().getDescription(), "草稿或驳回状态");
        }

        // 检查是否过期
        if (quote.getExpireDate() != null && quote.getExpireDate().isBefore(LocalDate.now())) {
            throw new BusinessException("报价单已过期，请先续期");
        }

        // 折扣阈值判断（模拟阈值 0.8）
        BigDecimal threshold = new BigDecimal("0.8");
        boolean needApproval = quote.getItems().stream()
                .anyMatch(item -> item.getDiscountRate().compareTo(threshold) < 0);

        if (needApproval) {
            quote.setStatus(QuoteStatus.PENDING_APPROVAL);
            quote.setApprovalStatus(ApprovalStatus.PENDING);
            log.info("报价单折扣超过阈值，进入审批流程, quoteId: {}", quoteId);
        } else {
            // 无需审批，直接通过
            quote.setStatus(QuoteStatus.APPROVED);
            quote.setApprovalStatus(ApprovalStatus.APPROVED);
            log.info("报价单未超过折扣阈值，自动审批通过, quoteId: {}", quoteId);
        }
        quoteRepository.save(quote);
    }

    /**
     * 审批通过
     */
    @Transactional
    public void approveQuote(Long quoteId, ApprovalRequest request) {
        SalesQuote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> BusinessException.notFound("报价单", quoteId));

        if (quote.getStatus() != QuoteStatus.PENDING_APPROVAL) {
            throw BusinessException.invalidStatus(quote.getStatus().getDescription(), "待审批状态");
        }

        quote.setStatus(QuoteStatus.APPROVED);
        quote.setApprovalStatus(ApprovalStatus.APPROVED);
        quoteRepository.save(quote);

        // 记录审批记录
        saveApprovalRecord(quote, ApprovalAction.APPROVE, request.getComment());
        log.info("报价单审批通过, quoteId: {}", quoteId);
    }

    /**
     * 审批驳回
     */
    @Transactional
    public void rejectQuote(Long quoteId, ApprovalRequest request) {
        SalesQuote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> BusinessException.notFound("报价单", quoteId));

        if (quote.getStatus() != QuoteStatus.PENDING_APPROVAL) {
            throw BusinessException.invalidStatus(quote.getStatus().getDescription(), "待审批状态");
        }

        quote.setStatus(QuoteStatus.REJECTED);
        quote.setApprovalStatus(ApprovalStatus.REJECTED);
        quoteRepository.save(quote);

        saveApprovalRecord(quote, ApprovalAction.REJECT, request.getComment());
        log.info("报价单审批驳回, quoteId: {}", quoteId);
    }

    /**
     * 报价单转销售订单
     */
    @Transactional
    public SalesOrder generateOrderFromQuote(Long quoteId) {
        SalesQuote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> BusinessException.notFound("报价单", quoteId));

        // 状态校验
        if (quote.getStatus() != QuoteStatus.APPROVED) {
            throw BusinessException.invalidStatus(quote.getStatus().getDescription(), "已审批");
        }
        if (quote.getExpireDate() != null && quote.getExpireDate().isBefore(LocalDate.now())) {
            throw new BusinessException("报价单已过期，无法生成订单");
        }
        if (quote.getStatus() == QuoteStatus.CONVERTED) {
            throw new BusinessException("报价单已生成过订单");
        }

        // 创建订单实体
        SalesOrder order = new SalesOrder();
        order.setOrderNo(codeGenerator.generateOrderNo());
        order.setQuoteId(quote.getId());
        order.setCustomerId(quote.getCustomerId());
        order.setContactId(quote.getContactId());
        order.setOrderDate(LocalDate.now());
        order.setDeliveryDate(quote.getExpireDate());
        order.setDiscountAmount(quote.getDiscountAmount());
        order.setOwnerUserId(quote.getOwnerUserId());
        order.setOrderStatus(com.yourcompany.sales.common.enums.OrderStatus.WAIT_STOCK);
        order.setPaymentStatus(com.yourcompany.sales.common.enums.PaymentStatus.UNPAID);

        // 复制明细
        List<SalesOrderItem> orderItems = new ArrayList<>();
        for (SalesQuoteItem quoteItem : quote.getItems()) {
            SalesOrderItem orderItem = new SalesOrderItem();
            orderItem.setOrder(order);
            orderItem.setSkuId(quoteItem.getSkuId());
            orderItem.setSkuNameSnapshot(quoteItem.getSkuNameSnapshot());
            orderItem.setQty(quoteItem.getQty());
            orderItem.setUnitPrice(quoteItem.getDealUnitPrice());
            orderItem.setTaxRate(quoteItem.getTaxRate());
            orderItem.setDiscountRate(quoteItem.getDiscountRate());
            orderItem.setLineAmount(quoteItem.getLineAmount());
            orderItem.setLockedQty(0);
            orderItem.setOutboundQty(0);
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);
        order.calculateTotal();

        SalesOrder savedOrder = orderRepository.save(order);

        // 更新报价单状态
        quote.setStatus(QuoteStatus.CONVERTED);
        quoteRepository.save(quote);

        log.info("报价单转订单成功, quoteId: {}, orderNo: {}", quoteId, savedOrder.getOrderNo());
        return savedOrder;
    }

    /**
     * 根据ID查询报价单
     */
    public QuoteResponse getQuoteById(Long id) {
        SalesQuote quote = quoteRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("报价单", id));
        return convertToResponse(quote);
    }

    /**
     * 分页查询报价单（略，可用 Specification）
     */

    // ---------- 私有辅助方法 ----------
    private void saveApprovalRecord(SalesQuote quote, ApprovalAction action, String comment) {
        ApprovalRecord record = ApprovalRecord.builder()
                .bizType(BizType.QUOTE)
                .bizId(quote.getId())
                .bizNo(quote.getQuoteNo())
                .applyUserId(quote.getOwnerUserId())
                .approverUserId(SecurityUtils.getCurrentUserId())
                .action(action)
                .comment(comment)
                .actionTime(LocalDateTime.now())
                .statusAfterAction(quote.getStatus().name())
                .build();
        approvalRecordRepository.save(record);
    }

    private QuoteResponse convertToResponse(SalesQuote quote) {
        QuoteResponse response = BeanCopyUtils.copyBean(quote, QuoteResponse.class);
        // 转换明细
        List<QuoteItemResponse> itemResponses = new ArrayList<>();
        for (SalesQuoteItem item : quote.getItems()) {
            QuoteItemResponse itemResp = BeanCopyUtils.copyBean(item, QuoteItemResponse.class);
            // 可在此填充 SKU 当前信息
            itemResponses.add(itemResp);
        }
        response.setItems(itemResponses);
        // TODO: 填充客户名称、负责人姓名等关联信息
        return response;
    }
}