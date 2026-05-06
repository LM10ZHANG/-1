package com.yourcompany.sales.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarningItemVO {

    private Long bizId;
    private String bizCode;
    private String bizName;
    private String warningType;
    private String warningMessage;
}
