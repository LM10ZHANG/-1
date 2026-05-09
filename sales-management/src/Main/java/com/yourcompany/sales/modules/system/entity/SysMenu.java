package com.yourcompany.sales.modules.system.entity;

import com.yourcompany.sales.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sys_menu")
public class SysMenu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id", nullable = false)
    private Long parentId = 0L;

    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;

    @Column(name = "menu_type", nullable = false, length = 20)
    private String menuType;

    @Column(name = "path", length = 255)
    private String path;

    @Column(name = "component", length = 255)
    private String component;

    @Column(name = "permission_code", length = 100)
    private String permissionCode;

    @Column(name = "sort_no", nullable = false)
    private Integer sortNo = 0;

    @Column(name = "status", nullable = false)
    private Integer status = 1;
}
