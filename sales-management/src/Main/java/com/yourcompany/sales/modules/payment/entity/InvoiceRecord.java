package com.yourcompany.sales.modules.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "invoice_record")
public class InvoiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String invoiceNo;

    private Long orderId;

    private String invoiceTitle;
    private String taxNo;

    private BigDecimal invoiceAmount;

    private String invoiceStatus; // WAIT / ISSUED / CANCELED

    private LocalDate invoiceDate;

    private LocalDateTime createdAt;
}