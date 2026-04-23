package com.yourcompany.sales.modules.dict.entity;

import com.yourcompany.sales.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 字典项实体
 */
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "sys_dict_item")
public class SysDictItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dict_code", nullable = false, length = 50)
    private String dictCode;

    @Column(name = "item_value", nullable = false, length = 50)
    private String itemValue;

    @Column(name = "item_label", nullable = false, length = 100)
    private String itemLabel;

    @Column(name = "sort_no", nullable = false)
    private Integer sortNo = 0;

    @Column(name = "status", nullable = false)
    private Integer status = 1;

    @Column(name = "remark", length = 255)
    private String remark;
}
