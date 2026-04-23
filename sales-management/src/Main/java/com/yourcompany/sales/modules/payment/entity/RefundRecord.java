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
@Table(name = "refund_record")
public class RefundRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String refundNo;

    private Long orderId;
    private Long paymentId;

    private BigDecimal refundAmount;
    private String refundReason;

    private LocalDateTime refundTime;

    private String status; // WAIT / FINISHED / REJECTED

    private LocalDateTime createdAt;
}