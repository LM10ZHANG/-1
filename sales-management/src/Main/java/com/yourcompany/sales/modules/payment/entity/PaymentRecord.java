package com.yourcompany.sales.modules.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "payment_record")
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String paymentNo;

    private Long orderId;
    private Long customerId;

    private BigDecimal payAmount;
    private String payMethod;

    private LocalDateTime payTime;

    private String voucherUrl;

    private Long operatorUserId;

    private String status; // VALID / REVERSED

    private String remark;

    private LocalDateTime createdAt;
}