-- ============================================================================
-- 销售管理系统 - 后端 A 初始化脚本
-- 范围：认证鉴权、RBAC、菜单/按钮权限、操作日志
-- 默认账号：admin / 123456
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
    `password_hash` VARCHAR(100) NOT NULL COMMENT 'BCrypt 加密密码',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `mobile` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用，0停用',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `created_at` DATETIME DEFAULT NULL COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新时间',
    `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
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
    `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
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
    `menu_type` VARCHAR(20) NOT NULL COMMENT '类型：MENU 菜单，BUTTON 按钮',
    `path` VARCHAR(255) DEFAULT NULL COMMENT '前端路由路径',
    `component` VARCHAR(255) DEFAULT NULL COMMENT '前端组件路径',
    `permission_code` VARCHAR(100) DEFAULT NULL COMMENT '权限码，如 system:user:list',
    `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用，0停用',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `created_at` DATETIME DEFAULT NULL COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新时间',
    `deleted_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0未删除，1已删除',
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

INSERT INTO `sys_role`
(`id`, `role_code`, `role_name`, `status`, `remark`, `created_at`, `deleted_flag`)
VALUES
(1, 'ADMIN', '系统管理员', 1, '系统初始化角色', NOW(), 0),
(2, 'SALES', '销售员', 1, '负责工作台和客户跟进', NOW(), 0),
(3, 'SALES_MANAGER', '销售经理', 1, '负责经营看板、商品与报价', NOW(), 0),
(4, 'WAREHOUSE', '仓库人员', 1, '负责库存与出入库', NOW(), 0),
(5, 'FINANCE', '财务人员', 1, '负责收款、发票、应收与退款', NOW(), 0),
(6, 'BOSS', '老板', 1, '负责经营看板与审批监督', NOW(), 0);

-- 明文密码：admin / 123456
-- BCrypt: $2a$10$wT1ly/wXeWCR5L8V4ZuG2eGwbGrJxw3G8oHJY5aCgbaxFsHVUGsdm
INSERT INTO `sys_user`
(`id`, `username`, `password_hash`, `real_name`, `status`, `created_at`, `deleted_flag`)
VALUES
(1, 'admin', '$2a$10$wT1ly/wXeWCR5L8V4ZuG2eGwbGrJxw3G8oHJY5aCgbaxFsHVUGsdm', '系统管理员', 1, NOW(), 0);

INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES (1, 1, 1);

INSERT INTO `sys_menu`
(`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission_code`, `sort_no`, `status`, `created_at`, `deleted_flag`)
VALUES
(1, 0, '系统管理', 'MENU', '/system', NULL, NULL, 1, 1, NOW(), 0),
(2, 1, '用户管理', 'MENU', '/system/users', NULL, 'system:user:list', 10, 1, NOW(), 0),
(3, 1, '新增用户', 'BUTTON', NULL, NULL, 'system:user:create', 11, 1, NOW(), 0),
(4, 1, '编辑用户', 'BUTTON', NULL, NULL, 'system:user:update', 12, 1, NOW(), 0),
(5, 1, '用户状态', 'BUTTON', NULL, NULL, 'system:user:status', 13, 1, NOW(), 0),
(6, 1, '角色管理', 'MENU', '/system/roles', NULL, 'system:role:list', 20, 1, NOW(), 0),
(7, 1, '新增角色', 'BUTTON', NULL, NULL, 'system:role:create', 21, 1, NOW(), 0),
(8, 1, '编辑角色', 'BUTTON', NULL, NULL, 'system:role:update', 22, 1, NOW(), 0),
(9, 1, '角色授权', 'BUTTON', NULL, NULL, 'system:role:menus', 23, 1, NOW(), 0),
(10, 1, '菜单管理', 'MENU', '/system/menus', NULL, 'system:menu:list', 30, 1, NOW(), 0),
(11, 1, '新增菜单', 'BUTTON', NULL, NULL, 'system:menu:create', 31, 1, NOW(), 0),
(12, 1, '编辑菜单', 'BUTTON', NULL, NULL, 'system:menu:update', 32, 1, NOW(), 0),
(13, 1, '删除菜单', 'BUTTON', NULL, NULL, 'system:menu:delete', 33, 1, NOW(), 0),
(14, 1, '操作日志', 'MENU', '/system/logs', NULL, 'system:log:list', 40, 1, NOW(), 0),
(15, 0, '工作台', 'MENU', '/dashboard', NULL, NULL, 100, 1, NOW(), 0),
(16, 15, '首页看板', 'MENU', '/dashboard/home', NULL, 'dashboard:overview', 110, 1, NOW(), 0),
(17, 15, '销售排行', 'MENU', '/dashboard/rankings', NULL, 'dashboard:rankings', 120, 1, NOW(), 0),
(18, 15, '系统预警', 'MENU', '/dashboard/warnings', NULL, 'dashboard:warnings', 130, 1, NOW(), 0),
(19, 15, '销售趋势', 'MENU', '/dashboard/sales-trend', NULL, 'dashboard:trend', 140, 1, NOW(), 0),
(20, 0, '库存中心', 'MENU', '/stock', NULL, NULL, 200, 1, NOW(), 0),
(21, 20, '库存台账', 'MENU', '/stock/ledger', NULL, 'stock:list', 210, 1, NOW(), 0),
(22, 20, '库存详情', 'BUTTON', NULL, NULL, 'stock:detail', 211, 1, NOW(), 0),
(23, 20, '锁定库存', 'BUTTON', NULL, NULL, 'stock:lock', 212, 1, NOW(), 0),
(24, 20, '释放库存', 'BUTTON', NULL, NULL, 'stock:release', 213, 1, NOW(), 0),
(25, 20, '出库管理', 'MENU', '/stock/outbound', NULL, 'outbound:list', 220, 1, NOW(), 0),
(26, 20, '执行出库', 'BUTTON', NULL, NULL, 'outbound:create', 221, 1, NOW(), 0),
(27, 20, '出库详情', 'BUTTON', NULL, NULL, 'outbound:detail', 222, 1, NOW(), 0),
(28, 20, '退货入库', 'BUTTON', NULL, NULL, 'stock:return:inbound', 230, 1, NOW(), 0),
(29, 0, '财务中心', 'MENU', '/finance', NULL, NULL, 300, 1, NOW(), 0),
(30, 29, '收款记录', 'MENU', '/finance/payments', NULL, 'payment:list', 310, 1, NOW(), 0),
(31, 29, '新增收款', 'BUTTON', NULL, NULL, 'payment:create', 311, 1, NOW(), 0),
(32, 29, '发票管理', 'MENU', '/finance/invoices', NULL, 'invoice:list', 320, 1, NOW(), 0),
(33, 29, '新增发票', 'BUTTON', NULL, NULL, 'invoice:create', 321, 1, NOW(), 0),
(34, 29, '应收账款', 'MENU', '/finance/receivables', NULL, 'receivable:list', 330, 1, NOW(), 0),
(35, 29, '退款管理', 'MENU', '/finance/refunds', NULL, 'refund:list', 340, 1, NOW(), 0),
(36, 29, '发起退款', 'BUTTON', NULL, NULL, 'refund:create', 341, 1, NOW(), 0),
(37, 29, '完成退款', 'BUTTON', NULL, NULL, 'refund:finish', 342, 1, NOW(), 0),
(38, 29, '驳回退款', 'BUTTON', NULL, NULL, 'refund:reject', 343, 1, NOW(), 0);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `id` FROM `sys_menu` WHERE `deleted_flag` = 0;

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 16), (2, 17), (2, 18),
(3, 16), (3, 17), (3, 18), (3, 19),
(4, 21), (4, 22), (4, 23), (4, 24), (4, 25), (4, 26), (4, 27), (4, 28),
(5, 30), (5, 31), (5, 32), (5, 33), (5, 34), (5, 35), (5, 36), (5, 37), (5, 38),
(6, 16), (6, 17), (6, 18), (6, 19);
