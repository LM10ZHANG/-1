package com.yourcompany.sales.modules.dashboard.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourcompany.sales.modules.dashboard.enums.DashboardScope;

import lombok.Data;

@Data
public class DashboardQuery {

    private DashboardScope scope = DashboardScope.TODAY;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
