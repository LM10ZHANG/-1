package com.yourcompany.sales.modules.product.service;

import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.product.dto.SkuPricingVo;
import com.yourcompany.sales.modules.product.entity.ProductSku;
import com.yourcompany.sales.modules.product.repository.ProductSkuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * SKU 查询门面服务 —— 提供给后端 C 的报价/订单模块使用。
 *
 * 目的：替换当前 QuoteService/OrderService 中硬编码的明细字段：
 *   - skuNameSnapshot = "商品名称快照"
 *   - originUnitPrice = new BigDecimal("100.00")
 *   - taxRate         = new BigDecimal("13.00")
 *
 * 业务规则（《需求文档》4.4）：
 *   - 创建报价/订单时，若 SKU 不存在或已停用，直接抛业务异常
 *   - 历史单据回查时用 getSnapshot，允许返回已停用的 SKU
 */
@Service
@RequiredArgsConstructor
public class SkuQueryService {

    private final ProductSkuRepository skuRepository;

    /**
     * 报价/订单创建明细时调用：获取可用的定价信息，SKU 不存在或已停用均抛异常。
     */
    @Transactional(readOnly = true)
    public SkuPricingVo getForPricing(Long skuId) {
        ProductSku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> BusinessException.notFound("SKU", skuId));
        if (sku.getDeletedFlag() != null && sku.getDeletedFlag() == 1) {
            throw BusinessException.notFound("SKU", skuId);
        }
        if (sku.getStatus() == null || sku.getStatus() != 1) {
            throw BusinessException.operationForbidden("SKU 已停用，无法创建报价/订单：" + sku.getSkuCode());
        }
        return SkuPricingVo.builder()
                .skuId(sku.getId())
                .skuName(sku.getSkuName())
                .salePrice(sku.getSalePrice() == null ? BigDecimal.ZERO : sku.getSalePrice())
                .taxRate(sku.getTaxRate() == null ? BigDecimal.ZERO : sku.getTaxRate())
                .status(sku.getStatus())
                .build();
    }

    /**
     * 历史单据详情使用：允许返回已停用/已删除的 SKU 快照信息。
     */
    @Transactional(readOnly = true)
    public Optional<SkuPricingVo> getSnapshot(Long skuId) {
        return skuRepository.findById(skuId).map(sku -> SkuPricingVo.builder()
                .skuId(sku.getId())
                .skuName(sku.getSkuName())
                .salePrice(sku.getSalePrice())
                .taxRate(sku.getTaxRate())
                .status(sku.getStatus())
                .build());
    }
}
