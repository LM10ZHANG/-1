package com.yourcompany.sales.modules.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增/更新联系人请求 DTO
 */
@Data
public class ContactRequest {

    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50)
    private String name;

    @Size(max = 20)
    private String mobile;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100)
    private String email;

    @Size(max = 50)
    private String position;

    @Size(max = 50)
    private String wechat;

    /** 1 = 主联系人 / 0 = 非主，默认 0 */
    private Integer isPrimary = 0;

    private String remark;
}
