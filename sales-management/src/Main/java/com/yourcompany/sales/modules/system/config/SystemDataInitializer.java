package com.yourcompany.sales.modules.system.config;

import com.yourcompany.sales.modules.system.entity.SysMenu;
import com.yourcompany.sales.modules.system.entity.SysRole;
import com.yourcompany.sales.modules.system.entity.SysRoleMenu;
import com.yourcompany.sales.modules.system.entity.SysUser;
import com.yourcompany.sales.modules.system.entity.SysUserRole;
import com.yourcompany.sales.modules.system.repository.SysMenuRepository;
import com.yourcompany.sales.modules.system.repository.SysRoleMenuRepository;
import com.yourcompany.sales.modules.system.repository.SysRoleRepository;
import com.yourcompany.sales.modules.system.repository.SysUserRepository;
import com.yourcompany.sales.modules.system.repository.SysUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SystemDataInitializer implements CommandLineRunner {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_SALES = "SALES";
    private static final String ROLE_SALES_MANAGER = "SALES_MANAGER";
    private static final String ROLE_WAREHOUSE = "WAREHOUSE";
    private static final String ROLE_FINANCE = "FINANCE";
    private static final String ROLE_BOSS = "BOSS";

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final SysMenuRepository menuRepository;
    private final SysUserRoleRepository userRoleRepository;
    private final SysRoleMenuRepository roleMenuRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, SysRole> roles = initRoles();
        SysRole adminRole = roles.get(ROLE_ADMIN);

        SysUser admin = initAdminUser();
        bindAdminRole(admin, adminRole);

        Map<String, SysMenu> menus = initMenus();
        bindRolePermissions(roles, menus);
    }

    private Map<String, SysRole> initRoles() {
        Map<String, SysRole> roles = new LinkedHashMap<>();
        roles.put(ROLE_ADMIN, ensureRole(ROLE_ADMIN, "系统管理员", "系统初始化角色"));
        roles.put(ROLE_SALES, ensureRole(ROLE_SALES, "销售员", "负责工作台和客户跟进"));
        roles.put(ROLE_SALES_MANAGER, ensureRole(ROLE_SALES_MANAGER, "销售经理", "负责经营看板、商品与报价"));
        roles.put(ROLE_WAREHOUSE, ensureRole(ROLE_WAREHOUSE, "仓库人员", "负责库存与出入库"));
        roles.put(ROLE_FINANCE, ensureRole(ROLE_FINANCE, "财务人员", "负责收款、发票、应收与退款"));
        roles.put(ROLE_BOSS, ensureRole(ROLE_BOSS, "老板", "负责经营看板与审批监督"));
        return roles;
    }

    private SysRole ensureRole(String roleCode, String roleName, String remark) {
        return roleRepository.findByRoleCodeAndDeletedFlag(roleCode, 0).orElseGet(() -> {
            SysRole role = new SysRole();
            role.setRoleCode(roleCode);
            role.setRoleName(roleName);
            role.setStatus(1);
            role.setRemark(remark);
            role.setCreatedAt(LocalDateTime.now());
            return roleRepository.save(role);
        });
    }

    private SysUser initAdminUser() {
        return userRepository.findByUsernameAndDeletedFlag("admin", 0).orElseGet(() -> {
            SysUser user = new SysUser();
            user.setUsername("admin");
            user.setRealName("系统管理员");
            user.setPasswordHash(passwordEncoder.encode("123456"));
            user.setStatus(1);
            user.setCreatedAt(LocalDateTime.now());
            return userRepository.save(user);
        });
    }

    private void bindAdminRole(SysUser admin, SysRole adminRole) {
        boolean alreadyBound = userRoleRepository.findByUserId(admin.getId()).stream()
                .anyMatch(item -> item.getRoleId().equals(adminRole.getId()));
        if (alreadyBound) {
            return;
        }
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(admin.getId());
        userRole.setRoleId(adminRole.getId());
        userRoleRepository.save(userRole);
    }

    private Map<String, SysMenu> initMenus() {
        Map<String, SysMenu> menus = new LinkedHashMap<>();

        SysMenu systemRoot = ensureMenu(0L, "系统管理", "MENU", "/system", null, null, 1);
        SysMenu dashboardRoot = ensureMenu(0L, "工作台", "MENU", "/dashboard", null, null, 100);
        SysMenu stockRoot = ensureMenu(0L, "库存中心", "MENU", "/stock", null, null, 200);
        SysMenu financeRoot = ensureMenu(0L, "财务中心", "MENU", "/finance", null, null, 300);

        putMenu(menus, ensureMenu(systemRoot.getId(), "用户管理", "MENU", "/system/users", null, "system:user:list", 10));
        putMenu(menus, ensureMenu(systemRoot.getId(), "新增用户", "BUTTON", null, null, "system:user:create", 11));
        putMenu(menus, ensureMenu(systemRoot.getId(), "编辑用户", "BUTTON", null, null, "system:user:update", 12));
        putMenu(menus, ensureMenu(systemRoot.getId(), "用户状态", "BUTTON", null, null, "system:user:status", 13));
        putMenu(menus, ensureMenu(systemRoot.getId(), "角色管理", "MENU", "/system/roles", null, "system:role:list", 20));
        putMenu(menus, ensureMenu(systemRoot.getId(), "新增角色", "BUTTON", null, null, "system:role:create", 21));
        putMenu(menus, ensureMenu(systemRoot.getId(), "编辑角色", "BUTTON", null, null, "system:role:update", 22));
        putMenu(menus, ensureMenu(systemRoot.getId(), "角色授权", "BUTTON", null, null, "system:role:menus", 23));
        putMenu(menus, ensureMenu(systemRoot.getId(), "菜单管理", "MENU", "/system/menus", null, "system:menu:list", 30));
        putMenu(menus, ensureMenu(systemRoot.getId(), "新增菜单", "BUTTON", null, null, "system:menu:create", 31));
        putMenu(menus, ensureMenu(systemRoot.getId(), "编辑菜单", "BUTTON", null, null, "system:menu:update", 32));
        putMenu(menus, ensureMenu(systemRoot.getId(), "删除菜单", "BUTTON", null, null, "system:menu:delete", 33));
        putMenu(menus, ensureMenu(systemRoot.getId(), "操作日志", "MENU", "/system/logs", null, "system:log:list", 40));

        putMenu(menus, ensureMenu(dashboardRoot.getId(), "首页看板", "MENU", "/dashboard/home", null, "dashboard:overview", 110));
        putMenu(menus, ensureMenu(dashboardRoot.getId(), "销售排行", "MENU", "/dashboard/rankings", null, "dashboard:rankings", 120));
        putMenu(menus, ensureMenu(dashboardRoot.getId(), "系统预警", "MENU", "/dashboard/warnings", null, "dashboard:warnings", 130));
        putMenu(menus, ensureMenu(dashboardRoot.getId(), "销售趋势", "MENU", "/dashboard/sales-trend", null, "dashboard:trend", 140));

        putMenu(menus, ensureMenu(stockRoot.getId(), "库存台账", "MENU", "/stock/ledger", null, "stock:list", 210));
        putMenu(menus, ensureMenu(stockRoot.getId(), "库存详情", "BUTTON", null, null, "stock:detail", 211));
        putMenu(menus, ensureMenu(stockRoot.getId(), "锁定库存", "BUTTON", null, null, "stock:lock", 212));
        putMenu(menus, ensureMenu(stockRoot.getId(), "释放库存", "BUTTON", null, null, "stock:release", 213));
        putMenu(menus, ensureMenu(stockRoot.getId(), "出库管理", "MENU", "/stock/outbound", null, "outbound:list", 220));
        putMenu(menus, ensureMenu(stockRoot.getId(), "执行出库", "BUTTON", null, null, "outbound:create", 221));
        putMenu(menus, ensureMenu(stockRoot.getId(), "出库详情", "BUTTON", null, null, "outbound:detail", 222));
        putMenu(menus, ensureMenu(stockRoot.getId(), "退货入库", "BUTTON", null, null, "stock:return:inbound", 230));

        putMenu(menus, ensureMenu(financeRoot.getId(), "收款记录", "MENU", "/finance/payments", null, "payment:list", 310));
        putMenu(menus, ensureMenu(financeRoot.getId(), "新增收款", "BUTTON", null, null, "payment:create", 311));
        putMenu(menus, ensureMenu(financeRoot.getId(), "发票管理", "MENU", "/finance/invoices", null, "invoice:list", 320));
        putMenu(menus, ensureMenu(financeRoot.getId(), "新增发票", "BUTTON", null, null, "invoice:create", 321));
        putMenu(menus, ensureMenu(financeRoot.getId(), "应收账款", "MENU", "/finance/receivables", null, "receivable:list", 330));
        putMenu(menus, ensureMenu(financeRoot.getId(), "退款管理", "MENU", "/finance/refunds", null, "refund:list", 340));
        putMenu(menus, ensureMenu(financeRoot.getId(), "发起退款", "BUTTON", null, null, "refund:create", 341));
        putMenu(menus, ensureMenu(financeRoot.getId(), "完成退款", "BUTTON", null, null, "refund:finish", 342));
        putMenu(menus, ensureMenu(financeRoot.getId(), "驳回退款", "BUTTON", null, null, "refund:reject", 343));

        return menus;
    }

    private SysMenu ensureMenu(Long parentId, String name, String type, String path, String component,
                               String permission, Integer sortNo) {
        SysMenu existing = StringUtils.hasText(permission)
                ? menuRepository.findByPermissionCodeAndDeletedFlag(permission, 0).orElse(null)
                : findMenuByNameAndParent(name, parentId);
        if (existing != null) {
            return existing;
        }
        SysMenu menu = new SysMenu();
        menu.setParentId(parentId);
        menu.setMenuName(name);
        menu.setMenuType(type);
        menu.setPath(path);
        menu.setComponent(component);
        menu.setPermissionCode(permission);
        menu.setSortNo(sortNo);
        menu.setStatus(1);
        menu.setCreatedAt(LocalDateTime.now());
        return menuRepository.save(menu);
    }

    private SysMenu findMenuByNameAndParent(String name, Long parentId) {
        return menuRepository.findByDeletedFlagOrderBySortNoAscIdAsc(0).stream()
                .filter(item -> item.getParentId().equals(parentId) && name.equals(item.getMenuName()))
                .findFirst()
                .orElse(null);
    }

    private void putMenu(Map<String, SysMenu> menus, SysMenu menu) {
        if (StringUtils.hasText(menu.getPermissionCode())) {
            menus.put(menu.getPermissionCode(), menu);
        }
    }

    private void bindRolePermissions(Map<String, SysRole> roles, Map<String, SysMenu> menus) {
        bindPermissions(roles.get(ROLE_ADMIN), menus.keySet(), menus);
        bindPermissions(roles.get(ROLE_SALES), Set.of(
                "dashboard:overview",
                "dashboard:rankings",
                "dashboard:warnings"
        ), menus);
        bindPermissions(roles.get(ROLE_SALES_MANAGER), Set.of(
                "dashboard:overview",
                "dashboard:rankings",
                "dashboard:warnings",
                "dashboard:trend"
        ), menus);
        bindPermissions(roles.get(ROLE_WAREHOUSE), Set.of(
                "stock:list",
                "stock:detail",
                "stock:lock",
                "stock:release",
                "outbound:list",
                "outbound:create",
                "outbound:detail",
                "stock:return:inbound"
        ), menus);
        bindPermissions(roles.get(ROLE_FINANCE), Set.of(
                "payment:list",
                "payment:create",
                "invoice:list",
                "invoice:create",
                "receivable:list",
                "refund:list",
                "refund:create",
                "refund:finish",
                "refund:reject"
        ), menus);
        bindPermissions(roles.get(ROLE_BOSS), Set.of(
                "dashboard:overview",
                "dashboard:rankings",
                "dashboard:warnings",
                "dashboard:trend"
        ), menus);
    }

    private void bindPermissions(SysRole role, Set<String> permissionCodes, Map<String, SysMenu> menus) {
        Set<Long> existingMenuIds = roleMenuRepository.findByRoleId(role.getId()).stream()
                .map(SysRoleMenu::getMenuId)
                .collect(java.util.stream.Collectors.toSet());
        for (String permissionCode : permissionCodes) {
            SysMenu menu = menus.get(permissionCode);
            if (menu == null || existingMenuIds.contains(menu.getId())) {
                continue;
            }
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(role.getId());
            roleMenu.setMenuId(menu.getId());
            roleMenuRepository.save(roleMenu);
        }
    }
}
