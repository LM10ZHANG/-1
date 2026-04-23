package com.yourcompany.sales.modules.dashboard.reposity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yourcompany.sales.modules.dashboard.entity.NotificationMessage;

public interface NotificationRepository
        extends JpaRepository<NotificationMessage, Long> {

    List<NotificationMessage> findByReadFlag(Integer readFlag);

    List<NotificationMessage> findByReceiverUserIdAndReadFlag(Long receiverUserId, Integer readFlag);

    boolean existsByBizTypeAndBizIdAndReadFlag(String bizType, Long bizId, Integer readFlag);
}
