package com.yourcompany.sales.modules.system.entity;

import com.yourcompany.sales.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sys_user")
public class SysUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "real_name", length = 50)
    private String realName;

    @Column(name = "mobile", length = 20)
    private String mobile;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "status", nullable = false)
    private Integer status = 1;
}
