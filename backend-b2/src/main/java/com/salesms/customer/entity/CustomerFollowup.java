package com.salesms.customer.entity;

import com.salesms.common.AuditFields;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_followup")
public class CustomerFollowup extends AuditFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;

    @Column(name = "follow_user_id")
    private Long followUserId;

    @Column(name = "follow_type", length = 20)
    private String followType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "next_follow_time")
    private LocalDateTime nextFollowTime;

    @Column(name = "follow_result", length = 50)
    private String followResult;

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Long getFollowUserId() {
        return followUserId;
    }

    public void setFollowUserId(Long followUserId) {
        this.followUserId = followUserId;
    }

    public String getFollowType() {
        return followType;
    }

    public void setFollowType(String followType) {
        this.followType = followType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getNextFollowTime() {
        return nextFollowTime;
    }

    public void setNextFollowTime(LocalDateTime nextFollowTime) {
        this.nextFollowTime = nextFollowTime;
    }

    public String getFollowResult() {
        return followResult;
    }

    public void setFollowResult(String followResult) {
        this.followResult = followResult;
    }
}

