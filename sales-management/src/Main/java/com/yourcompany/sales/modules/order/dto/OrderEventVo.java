package com.yourcompany.sales.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单时间线事件对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventVo {

    private String eventType;       // 事件类型：CREATE, SUBMIT_APPROVAL, APPROVED, LOCK_STOCK, OUTBOUND, PAYMENT 等
    private String description;     // 事件描述
    private String operator;        // 操作人姓名
    private LocalDateTime eventTime; // 事件发生时间
}