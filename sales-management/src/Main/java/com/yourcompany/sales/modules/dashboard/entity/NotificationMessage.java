package com.yourcompany.sales.modules.dashboard.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "notification_message")
public class NotificationMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long receiverUserId;

    private String msgType;   // STOCK_WARN / PAYMENT_WARN
    private String title;
    private String content;

    private String bizType;   // STOCK / ORDER / PAYMENT
    private Long bizId;

    private Integer readFlag; // 0未读 1已读

    private LocalDateTime sendTime;
}