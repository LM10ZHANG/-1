package com.yourcompany.sales.modules.dashboard.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankingItemVO {

    private Long bizId;
    private String bizName;
    private BigDecimal amount;
    private Integer count;
    private Integer rankNo;
}
