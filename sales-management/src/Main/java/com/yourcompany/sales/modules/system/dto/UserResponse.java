package com.yourcompany.sales.modules.system.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String realName;
    private String mobile;
    private String email;
    private Integer status;
    private List<String> roles;
    private List<String> permissions;
}
