-- ============================================================================
-- 销售管理系统 - 后端 A 技术底座建表脚本
-- 范围：认证鉴权、RBAC、菜单/按钮权限、操作日志
-- 数据库：MySQL 8.x
-- 默认账号：admin / 123456
-- 说明：默认密码已使用 BCrypt 加密，不可明文存储。
-- ============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------------------------------------------------------
-- 1. 系统用户表 sys_user
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '登录用户名',
    `password_hash` VARCHAR(100) NOT NULL COMMENT 'BCrypt 加密后的密码',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `mobile` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用，0停用',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `created_at` DATETIME DEFAULT NULL COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新时间',
    `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_user_username` (`username`),
    KEY `idx_sys_user_status` (`status`),
    KEY `idx_sys_user_deleted` (`deleted_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';

-- ----------------------------------------------------------------------------
-- 2. 系统角色表 sys_role
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码，如 ADMIN、SALES',
    `role_name` VARCHAR(100) NOT NULL COMMENT '角色名称',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用，0停用',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `created_at` DATETIME DEFAULT NULL COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新时间',
    `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_role_code` (`role_code`),
    KEY `idx_sys_role_status` (`status`),
    KEY `idx_sys_role_deleted` (`deleted_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色表';

-- ----------------------------------------------------------------------------
-- 3. 用户角色关联表 sys_user_role
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_user_role` (`user_id`, `role_id`),
    KEY `idx_sys_user_role_user` (`user_id`),
    KEY `idx_sys_user_role_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';

-- ----------------------------------------------------------------------------
-- 4. 菜单/按钮权限表 sys_menu
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父级菜单ID，0表示根节点',
    `menu_name` VARCHAR(100) NOT NULL COMMENT '菜单或按钮名称',
    `menu_type` VARCHAR(20) NOT NULL COMMENT '类型：MENU菜单，BUTTON按钮',
    `path` VARCHAR(255) DEFAULT NULL COMMENT '前端路由路径',
    `component` VARCHAR(255) DEFAULT NULL COMMENT '前端组件路径',
    `permission_code` VARCHAR(100) DEFAULT NULL COMMENT '权限码，如 system:user:list',
    `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用，0停用',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `created_at` DATETIME DEFAULT NULL COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新时间',
    `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (`id`),
    KEY `idx_sys_menu_parent` (`parent_id`),
    KEY `idx_sys_menu_permission` (`permission_code`),
    KEY `idx_sys_menu_type` (`menu_type`),
    KEY `idx_sys_menu_deleted` (`deleted_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单与按钮权限表';

-- ----------------------------------------------------------------------------
-- 5. 角色菜单关联表 sys_role_menu
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单或按钮ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_role_menu` (`role_id`, `menu_id`),
    KEY `idx_sys_role_menu_role` (`role_id`),
    KEY `idx_sys_role_menu_menu` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色菜单权限关联表';

-- ----------------------------------------------------------------------------
-- 6. 操作日志表 operation_log
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `module_name` VARCHAR(50) DEFAULT NULL COMMENT '模块名称',
    `action_name` VARCHAR(50) DEFAULT NULL COMMENT '操作名称',
    `biz_type` VARCHAR(50) DEFAULT NULL COMMENT '业务类型',
    `operator_user_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `operator_username` VARCHAR(50) DEFAULT NULL COMMENT '操作用户名',
    `request_method` VARCHAR(10) DEFAULT NULL COMMENT '请求方法',
    `request_uri` VARCHAR(255) DEFAULT NULL COMMENT '请求路径',
    `client_ip` VARCHAR(64) DEFAULT NULL COMMENT '客户端IP',
    `trace_id` VARCHAR(64) DEFAULT NULL COMMENT '链路追踪ID',
    `success_flag` TINYINT DEFAULT NULL COMMENT '是否成功：1成功，0失败',
    `error_message` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    `cost_ms` BIGINT DEFAULT NULL COMMENT '耗时毫秒',
    `created_at` DATETIME DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_operation_log_created_at` (`created_at`),
    KEY `idx_operation_log_operator` (`operator_user_id`),
    KEY `idx_operation_log_module` (`module_name`),
    KEY `idx_operation_log_trace` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志表';

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- 初始化数据
-- ============================================================================

-- 默认管理员角色
INSERT INTO `sys_role`
(`id`, `role_code`, `role_name`, `status`, `remark`, `created_at`, `deleted_flag`)
VALUES
(1, 'ADMIN', '系统管理员', 1, '系统初始化角色', NOW(), 0);

-- 默认管理员用户
-- 明文密码：admin / 123456
-- BCrypt: $2a$10$wT1ly/wXeWCR5L8V4ZuG2eGwbGrJxw3G8oHJY5aCgbaxFsHVUGsdm
INSERT INTO `sys_user`
(`id`, `username`, `password_hash`, `real_name`, `status`, `created_at`, `deleted_flag`)
VALUES
(1, 'admin', '$2a$10$wT1ly/wXeWCR5L8V4ZuG2eGwbGrJxw3G8oHJY5aCgbaxFsHVUGsdm', '系统管理员', 1, NOW(), 0);

-- 绑定 admin 用户到 ADMIN 角色
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES (1, 1, 1);

-- 初始化系统管理菜单和按钮权限
INSERT INTO `sys_menu`
(`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission_code`, `sort_no`, `status`, `created_at`, `deleted_flag`)
VALUES
(1, 0, '系统管理', 'MENU', '/system', NULL, NULL, 1, 1, NOW(), 0),
(2, 1, '用户管理', 'MENU', '/system/users', NULL, 'system:user:list', 10, 1, NOW(), 0),
(3, 2, '新增用户', 'BUTTON', NULL, NULL, 'system:user:create', 11, 1, NOW(), 0),
(4, 2, '编辑用户', 'BUTTON', NULL, NULL, 'system:user:update', 12, 1, NOW(), 0),
(5, 2, '用户状态', 'BUTTON', NULL, NULL, 'system:user:status', 13, 1, NOW(), 0),
(6, 1, '角色管理', 'MENU', '/system/roles', NULL, 'system:role:list', 20, 1, NOW(), 0),
(7, 6, '新增角色', 'BUTTON', NULL, NULL, 'system:role:create', 21, 1, NOW(), 0),
(8, 6, '编辑角色', 'BUTTON', NULL, NULL, 'system:role:update', 22, 1, NOW(), 0),
(9, 6, '角色授权', 'BUTTON', NULL, NULL, 'system:role:menus', 23, 1, NOW(), 0),
(10, 1, '菜单管理', 'MENU', '/system/menus', NULL, 'system:menu:list', 30, 1, NOW(), 0),
(11, 10, '新增菜单', 'BUTTON', NULL, NULL, 'system:menu:create', 31, 1, NOW(), 0),
(12, 10, '编辑菜单', 'BUTTON', NULL, NULL, 'system:menu:update', 32, 1, NOW(), 0),
(13, 10, '删除菜单', 'BUTTON', NULL, NULL, 'system:menu:delete', 33, 1, NOW(), 0),
(14, 1, '操作日志', 'MENU', '/system/logs', NULL, 'system:log:list', 40, 1, NOW(), 0);

-- ADMIN 角色拥有全部初始化菜单和按钮权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `id` FROM `sys_menu` WHERE `deleted_flag` = 0;

-- ============================================================================
-- 使用说明
-- 1. 如果已由 Hibernate ddl-auto 自动建表，本脚本可用于正式 MySQL 环境重建表结构。
-- 2. 如果与其他后端成员 SQL 合并，请先执行公共/系统表，再执行业务表。
-- 3. 如果已有数据，请不要直接执行 DROP TABLE 版本，需改为迁移脚本。
-- ============================================================================
