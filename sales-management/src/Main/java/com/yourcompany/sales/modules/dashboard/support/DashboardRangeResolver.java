package com.yourcompany.sales.modules.dashboard.support;

import java.time.DayOfWeek;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.yourcompany.sales.common.exception.ValidationException;
import com.yourcompany.sales.modules.dashboard.enums.DashboardScope;

@Component
public class DashboardRangeResolver {

    public TimeRange resolve(DashboardScope scope, LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();

        if (scope == null) {
            scope = DashboardScope.TODAY;
        }

        return switch (scope) {
            case TODAY -> new TimeRange(
                    today.atStartOfDay(),
                    today.atTime(23, 59, 59));

            case WEEK -> {
                LocalDate monday = today.with(DayOfWeek.MONDAY);
                LocalDate sunday = monday.plusDays(6);
                yield new TimeRange(
                        monday.atStartOfDay(),
                        sunday.atTime(23, 59, 59));
            }

            case MONTH -> {
                LocalDate firstDay = today.withDayOfMonth(1);
                LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());
                yield new TimeRange(
                        firstDay.atStartOfDay(),
                        lastDay.atTime(23, 59, 59));
            }

            case CUSTOM -> {
                if (startDate == null || endDate == null) {
                    throw new ValidationException("自定义区间必须传 startDate 和 endDate");
                }
                if (endDate.isBefore(startDate)) {
                    throw new ValidationException("endDate 不能早于 startDate");
                }
                yield new TimeRange(
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59));
            }
        };
    }
}
