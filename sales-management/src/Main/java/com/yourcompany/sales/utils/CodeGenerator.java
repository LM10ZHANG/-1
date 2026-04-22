package com.yourcompany.sales.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 业务单号生成器
 */
@Component
public class CodeGenerator {

    // 简单自增序号，实际项目中应该使用数据库序列或 Redis 原子递增
    private final AtomicInteger quoteSerial = new AtomicInteger(1);
    private final AtomicInteger orderSerial = new AtomicInteger(1);
    private final AtomicInteger outboundSerial = new AtomicInteger(1);
    private final AtomicInteger paymentSerial = new AtomicInteger(1);

    /**
     * 生成报价单号，格式：QT + yyyyMMdd + 4位流水号，例如 QT202503210001
     */
    public String generateQuoteNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = quoteSerial.getAndIncrement() % 10000;
        return String.format("QT%s%04d", date, seq);
    }

    /**
     * 生成销售订单号，格式：SO + yyyyMMdd + 4位流水号，例如 SO202503210001
     */
    public String generateOrderNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = orderSerial.getAndIncrement() % 10000;
        return String.format("SO%s%04d", date, seq);
    }

    /**
     * 生成出库单号，格式：OUT + yyyyMMdd + 4位流水号
     */
    public String generateOutboundNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = outboundSerial.getAndIncrement() % 10000;
        return String.format("OUT%s%04d", date, seq);
    }

    /**
     * 生成收款单号，格式：PAY + yyyyMMdd + 4位流水号
     */
    public String generatePaymentNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = paymentSerial.getAndIncrement() % 10000;
        return String.format("PAY%s%04d", date, seq);
    }
}