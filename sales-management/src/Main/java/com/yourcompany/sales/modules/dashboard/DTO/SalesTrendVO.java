package com.yourcompany.sales.modules.dashboard.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesTrendVO {

    private LocalDate date;          // 日期
    private BigDecimal orderAmount;  // 订单金额
    private BigDecimal paymentAmount;// 收款金额
}