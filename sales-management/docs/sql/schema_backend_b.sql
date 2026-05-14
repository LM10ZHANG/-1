-- ============================================================================
-- 销售管理系统 - 后端 B 主数据表建表脚本
-- 范围：客户、联系人、跟进、商品 SPU/SKU、商品分类、字典
-- 依据：《销售管理系统需求文档》第 7.5~7.9 节 + 《分工文档》4.2
-- 约定：字符集 utf8mb4；金额统一 DECIMAL(18,2)；审计字段统一 created_by/at、updated_by/at、deleted_flag
-- ============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------------------------------------------------------
-- 1. 客户表 customer （文档 7.5）
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
    `id`                  BIGINT          NOT NULL AUTO_INCREMENT COMMENT '客户主键',
    `customer_code`       VARCHAR(50)     NOT NULL COMMENT '客户编码',
    `customer_name`       VARCHAR(120)    NOT NULL COMMENT '客户名称',
    `customer_level`      VARCHAR(20)     DEFAULT NULL COMMENT 'A/B/C 或会员等级',
    `customer_type`       VARCHAR(20)     DEFAULT NULL COMMENT '企业/个人/渠道',
    `industry`            VARCHAR(50)     DEFAULT NULL COMMENT '所属行业',
    `source`              VARCHAR(50)     DEFAULT NULL COMMENT '客户来源',
    `province`            VARCHAR(50)     DEFAULT NULL COMMENT '省',
    `city`                VARCHAR(50)     DEFAULT NULL COMMENT '市',
    `address`             VARCHAR(255)    DEFAULT NULL COMMENT '详细地址',
    `owner_user_id`       BIGINT          DEFAULT NULL COMMENT '负责人用户ID',
    `credit_limit`        DECIMAL(18,2)   DEFAULT 0 COMMENT '信用额度',
    `current_ar_amount`   DECIMAL(18,2)   DEFAULT 0 COMMENT '当前应收金额',
    `follow_status`       VARCHAR(20)     DEFAULT NULL COMMENT '跟进状态',
    `status`              TINYINT         NOT NULL DEFAULT 1 COMMENT '1 正常 / 0 禁用',
    `remark`              VARCHAR(255)    DEFAULT NULL COMMENT '备注',
    `created_by`          BIGINT          DEFAULT NULL COMMENT '创建人',
    `created_at`          DATETIME        DEFAULT NULL COMMENT '创建时间',
    `updated_by`          BIGINT          DEFAULT NULL COMMENT '更新人',
    `updated_at`          DATETIME        DEFAULT NULL COMMENT '更新时间',
    `deleted_flag`        TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_customer_code` (`customer_code`),
    KEY `idx_customer_name` (`customer_name`),
    KEY `idx_customer_owner` (`owner_user_id`),
    KEY `idx_customer_created_at` (`created_at`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '客户主表';

-- ----------------------------------------------------------------------------
-- 2. 客户联系人表 customer_contact （文档 7.6）
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `customer_contact`;
CREATE TABLE `customer_contact` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '联系人主键',
    `customer_id`   BIGINT       NOT NULL COMMENT '所属客户ID',
    `name`          VARCHAR(50)  NOT NULL COMMENT '联系人姓名',
    `mobile`        VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`         VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `position`      VARCHAR(50)  DEFAULT NULL COMMENT '职位',
    `wechat`        VARCHAR(50)  DEFAULT NULL COMMENT '微信/IM',
    `is_primary`    TINYINT      NOT NULL DEFAULT 0 COMMENT '是否主联系人',
    `remark`        VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `created_by`    BIGINT       DEFAULT NULL,
    `created_at`    DATETIME     DEFAULT NULL,
    `updated_by`    BIGINT       DEFAULT NULL,
    `updated_at`    DATETIME     DEFAULT NULL,
    `deleted_flag`  TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_contact_customer` (`customer_id`),
    KEY `idx_contact_mobile` (`mobile`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '客户联系人表';

-- ----------------------------------------------------------------------------
-- 3. 客户跟进记录表 customer_followup （文档 7.7）
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `customer_followup`;
CREATE TABLE `customer_followup` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '跟进记录主键',
    `customer_id`       BIGINT       NOT NULL COMMENT '客户ID',
    `follow_user_id`    BIGINT       DEFAULT NULL COMMENT '跟进人',
    `follow_type`       VARCHAR(20)  DEFAULT NULL COMMENT '电话/拜访/微信/邮件',
    `content`           TEXT         COMMENT '跟进内容',
    `next_follow_time`  DATETIME     DEFAULT NULL COMMENT '下次跟进时间',
    `follow_result`     VARCHAR(50)  DEFAULT NULL COMMENT '跟进结果',
    `created_by`        BIGINT       DEFAULT NULL,
    `created_at`        DATETIME     DEFAULT NULL,
    `updated_by`        BIGINT       DEFAULT NULL,
    `updated_at`        DATETIME     DEFAULT NULL,
    `deleted_flag`      TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_followup_customer` (`customer_id`),
    KEY `idx_followup_user` (`follow_user_id`),
    KEY `idx_followup_created_at` (`created_at`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '客户跟进记录表';

-- ----------------------------------------------------------------------------
-- 4. 商品分类表 product_category （分工文档 4.2）
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类主键',
    `parent_id`      BIGINT       NOT NULL DEFAULT 0 COMMENT '父分类ID，0 表示根',
    `category_code`  VARCHAR(50)  NOT NULL COMMENT '分类编码',
    `category_name`  VARCHAR(100) NOT NULL COMMENT '分类名称',
    `sort_no`        INT          NOT NULL DEFAULT 0 COMMENT '排序号',
    `status`         TINYINT      NOT NULL DEFAULT 1 COMMENT '1 启用 / 0 停用',
    `remark`         VARCHAR(255) DEFAULT NULL,
    `created_by`     BIGINT       DEFAULT NULL,
    `created_at`     DATETIME     DEFAULT NULL,
    `updated_by`     BIGINT       DEFAULT NULL,
    `updated_at`     DATETIME     DEFAULT NULL,
    `deleted_flag`   TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_code` (`category_code`),
    KEY `idx_category_parent` (`parent_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '商品分类表';

-- ----------------------------------------------------------------------------
-- 5. 商品 SPU 表 product_spu （文档 7.8）
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `product_spu`;
CREATE TABLE `product_spu` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'SPU 主键',
    `spu_code`      VARCHAR(50)  NOT NULL COMMENT 'SPU 编码',
    `spu_name`      VARCHAR(120) NOT NULL COMMENT '商品名称',
    `category_id`   BIGINT       DEFAULT NULL COMMENT '分类ID',
    `brand_name`    VARCHAR(50)  DEFAULT NULL COMMENT '品牌',
    `unit_name`     VARCHAR(20)  DEFAULT NULL COMMENT '计量单位',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '启用状态',
    `description`   TEXT         COMMENT '商品描述',
    `created_by`    BIGINT       DEFAULT NULL,
    `created_at`    DATETIME     DEFAULT NULL,
    `updated_by`    BIGINT       DEFAULT NULL,
    `updated_at`    DATETIME     DEFAULT NULL,
    `deleted_flag`  TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_spu_code` (`spu_code`),
    KEY `idx_spu_name` (`spu_name`),
    KEY `idx_spu_category` (`category_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '商品 SPU 表';

-- ----------------------------------------------------------------------------
-- 6. 商品 SKU 表 product_sku （文档 7.9）
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `product_sku`;
CREATE TABLE `product_sku` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'SKU 主键',
    `spu_id`          BIGINT        NOT NULL COMMENT '所属 SPU ID',
    `sku_code`        VARCHAR(50)   NOT NULL COMMENT 'SKU 编码',
    `sku_name`        VARCHAR(120)  NOT NULL COMMENT 'SKU 名称',
    `spec_json`       TEXT          COMMENT '规格 JSON，如颜色/尺码',
    `barcode`         VARCHAR(50)   DEFAULT NULL COMMENT '条码',
    `sale_price`      DECIMAL(18,2) DEFAULT 0 COMMENT '默认销售价',
    `cost_price`      DECIMAL(18,2) DEFAULT 0 COMMENT '成本价',
    `tax_rate`        DECIMAL(5,2)  DEFAULT 0 COMMENT '默认税率（百分比，如 13.00）',
    `stock_warn_qty`  INT           DEFAULT 0 COMMENT '库存预警值',
    `status`          TINYINT       NOT NULL DEFAULT 1 COMMENT '启用状态',
    `created_by`      BIGINT        DEFAULT NULL,
    `created_at`      DATETIME      DEFAULT NULL,
    `updated_by`      BIGINT        DEFAULT NULL,
    `updated_at`      DATETIME      DEFAULT NULL,
    `deleted_flag`    TINYINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sku_code` (`sku_code`),
    KEY `idx_sku_spu` (`spu_id`),
    KEY `idx_sku_name` (`sku_name`),
    KEY `idx_sku_barcode` (`barcode`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '商品 SKU 表';

-- ----------------------------------------------------------------------------
-- 7. 字典主表 sys_dict
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '字典主键',
    `dict_code`    VARCHAR(50)  NOT NULL COMMENT '字典编码，如 CUSTOMER_LEVEL',
    `dict_name`    VARCHAR(100) NOT NULL COMMENT '字典名称',
    `status`       TINYINT      NOT NULL DEFAULT 1,
    `remark`       VARCHAR(255) DEFAULT NULL,
    `created_by`   BIGINT       DEFAULT NULL,
    `created_at`   DATETIME     DEFAULT NULL,
    `updated_by`   BIGINT       DEFAULT NULL,
    `updated_at`   DATETIME     DEFAULT NULL,
    `deleted_flag` TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_code` (`dict_code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '字典主表';

-- ----------------------------------------------------------------------------
-- 8. 字典项表 sys_dict_item
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '字典项主键',
    `dict_code`    VARCHAR(50)  NOT NULL COMMENT '所属字典编码',
    `item_value`   VARCHAR(50)  NOT NULL COMMENT '字典项值',
    `item_label`   VARCHAR(100) NOT NULL COMMENT '字典项显示文本',
    `sort_no`      INT          NOT NULL DEFAULT 0,
    `status`       TINYINT      NOT NULL DEFAULT 1,
    `remark`       VARCHAR(255) DEFAULT NULL,
    `created_by`   BIGINT       DEFAULT NULL,
    `created_at`   DATETIME     DEFAULT NULL,
    `updated_by`   BIGINT       DEFAULT NULL,
    `updated_at`   DATETIME     DEFAULT NULL,
    `deleted_flag` TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_dict_item_code` (`dict_code`, `item_value`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '字典项表';

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- 初始化字典数据（可选，便于前端联调）
-- ============================================================================
INSERT INTO `sys_dict` (`dict_code`, `dict_name`, `status`, `created_at`) VALUES
    ('CUSTOMER_LEVEL',  '客户等级', 1, NOW()),
    ('CUSTOMER_TYPE',   '客户类型', 1, NOW()),
    ('CUSTOMER_SOURCE', '客户来源', 1, NOW()),
    ('FOLLOW_TYPE',     '跟进方式', 1, NOW()),
    ('PRODUCT_UNIT',    '商品计量单位', 1, NOW());

INSERT INTO `sys_dict_item` (`dict_code`, `item_value`, `item_label`, `sort_no`, `status`, `created_at`) VALUES
    ('CUSTOMER_LEVEL',  'A', 'A 级 - 重点客户', 1, 1, NOW()),
    ('CUSTOMER_LEVEL',  'B', 'B 级 - 一般客户', 2, 1, NOW()),
    ('CUSTOMER_LEVEL',  'C', 'C 级 - 潜在客户', 3, 1, NOW()),
    ('CUSTOMER_TYPE',   'ENTERPRISE', '企业',  1, 1, NOW()),
    ('CUSTOMER_TYPE',   'PERSONAL',   '个人',  2, 1, NOW()),
    ('CUSTOMER_TYPE',   'CHANNEL',    '渠道',  3, 1, NOW()),
    ('CUSTOMER_SOURCE', 'ONLINE',     '网络推广', 1, 1, NOW()),
    ('CUSTOMER_SOURCE', 'REFERRAL',   '客户推荐', 2, 1, NOW()),
    ('CUSTOMER_SOURCE', 'EXHIBITION', '展会',    3, 1, NOW()),
    ('FOLLOW_TYPE',     'PHONE',   '电话', 1, 1, NOW()),
    ('FOLLOW_TYPE',     'VISIT',   '拜访', 2, 1, NOW()),
    ('FOLLOW_TYPE',     'WECHAT',  '微信', 3, 1, NOW()),
    ('FOLLOW_TYPE',     'EMAIL',   '邮件', 4, 1, NOW()),
    ('PRODUCT_UNIT',    'PCS',   '件', 1, 1, NOW()),
    ('PRODUCT_UNIT',    'BOX',   '箱', 2, 1, NOW()),
    ('PRODUCT_UNIT',    'KG',    '千克', 3, 1, NOW());

-- ============================================================================
-- 三、追加菜单数据（与 schema_backend_a.sql 中的菜单合并使用）
--    后端 A 的 sys_menu 初始化不包含 客户/商品/字典 菜单，本节予以补齐。
--    实际项目启动会由 MasterDataInitializer.java 自动幂等灌入；
--    如果直连 MySQL 离线建库，可执行下面 SQL 一次性导入。
-- ============================================================================
INSERT INTO `sys_menu`
(`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission_code`, `sort_no`, `status`, `created_at`, `deleted_flag`)
VALUES
-- 客户中心
(0, '客户中心', 'MENU',   '/customer',           NULL, NULL,                       400, 1, NOW(), 0),
(LAST_INSERT_ID(), '客户列表',     'MENU',   '/customer/list',      NULL, 'customer:list',            410, 1, NOW(), 0),
(@root_customer := LAST_INSERT_ID() - 1, '新增客户', 'BUTTON', NULL,             NULL, 'customer:create',          411, 1, NOW(), 0),
(@root_customer, '编辑客户',  'BUTTON', NULL,                  NULL, 'customer:update',          412, 1, NOW(), 0),
(@root_customer, '客户状态',  'BUTTON', NULL,                  NULL, 'customer:status',          413, 1, NOW(), 0),
(@root_customer, '删除客户',  'BUTTON', NULL,                  NULL, 'customer:delete',          414, 1, NOW(), 0),
(@root_customer, '客户详情',  'MENU',   '/customer/detail',    NULL, 'customer:detail',          415, 1, NOW(), 0),
(@root_customer, '联系人管理','BUTTON', NULL,                  NULL, 'customer:contact:manage',  416, 1, NOW(), 0),
(@root_customer, '新增跟进',  'BUTTON', NULL,                  NULL, 'customer:followup:create', 417, 1, NOW(), 0),
(@root_customer, '跟进记录',  'MENU',   '/customer/followups', NULL, 'customer:followup:list',   418, 1, NOW(), 0),
-- 商品中心
(0, '商品中心', 'MENU', '/product', NULL, NULL, 500, 1, NOW(), 0),
(@root_product := LAST_INSERT_ID(), 'SPU 列表', 'MENU', '/product/spu', NULL, 'product:spu:list', 510, 1, NOW(), 0),
(@root_product, '新增 SPU', 'BUTTON', NULL, NULL, 'product:spu:create', 511, 1, NOW(), 0),
(@root_product, '编辑 SPU', 'BUTTON', NULL, NULL, 'product:spu:update', 512, 1, NOW(), 0),
(@root_product, 'SKU 列表', 'MENU',   '/product/sku', NULL, 'product:sku:list', 520, 1, NOW(), 0),
(@root_product, '新增 SKU', 'BUTTON', NULL, NULL, 'product:sku:create', 521, 1, NOW(), 0),
(@root_product, '编辑 SKU', 'BUTTON', NULL, NULL, 'product:sku:update', 522, 1, NOW(), 0),
(@root_product, 'SKU 状态', 'BUTTON', NULL, NULL, 'product:sku:status', 523, 1, NOW(), 0),
(@root_product, '商品分类', 'MENU',   '/product/categories', NULL, 'product:category:list',   530, 1, NOW(), 0),
(@root_product, '新增分类', 'BUTTON', NULL, NULL, 'product:category:create', 531, 1, NOW(), 0),
(@root_product, '编辑分类', 'BUTTON', NULL, NULL, 'product:category:update', 532, 1, NOW(), 0);

-- 字典管理挂在"系统管理"下（id=1，与 schema_backend_a.sql 保持一致）
INSERT INTO `sys_menu`
(`parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission_code`, `sort_no`, `status`, `created_at`, `deleted_flag`)
VALUES
(1, '字典管理',   'MENU',   '/system/dicts', NULL, 'system:dict:list',   50, 1, NOW(), 0),
(1, '新增字典',   'BUTTON', NULL,            NULL, 'system:dict:create', 51, 1, NOW(), 0),
(1, '编辑字典',   'BUTTON', NULL,            NULL, 'system:dict:update', 52, 1, NOW(), 0),
(1, '字典项管理', 'BUTTON', NULL,            NULL, 'system:dict:item',   53, 1, NOW(), 0);

-- 角色 - 菜单授权（ADMIN 已通过 schema_backend_a.sql 全量授权，这里只补销售员/销售经理）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, m.id FROM `sys_menu` m
WHERE m.permission_code IN (
    'customer:list','customer:create','customer:update','customer:status','customer:delete',
    'customer:detail','customer:contact:manage','customer:followup:create','customer:followup:list',
    'product:spu:list','product:spu:create','product:spu:update',
    'product:sku:list','product:sku:create','product:sku:update','product:sku:status',
    'product:category:list','product:category:create','product:category:update',
    'system:dict:list','system:dict:create','system:dict:update','system:dict:item'
);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 2, m.id FROM `sys_menu` m  -- 销售员 SALES
WHERE m.permission_code IN (
    'customer:list','customer:create','customer:update','customer:status',
    'customer:detail','customer:contact:manage','customer:followup:create','customer:followup:list',
    'product:spu:list','product:sku:list','product:category:list'
);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 3, m.id FROM `sys_menu` m  -- 销售经理 SALES_MANAGER
WHERE m.permission_code IN (
    'customer:list','customer:create','customer:update','customer:status','customer:delete',
    'customer:detail','customer:contact:manage','customer:followup:create','customer:followup:list',
    'product:spu:list','product:spu:create','product:spu:update',
    'product:sku:list','product:sku:create','product:sku:update','product:sku:status',
    'product:category:list','product:category:create','product:category:update'
);
