package com.salesms.customer.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class CustomerFollowupCreateRequest {

    private Long followUserId;

    @NotBlank
    private String followType; // 电话/拜访/微信/邮件

    @NotBlank
    private String content;

    private LocalDateTime nextFollowTime;

    private String followResult;

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

