package com.yourcompany.sales.modules.customer.config;

import com.yourcompany.sales.modules.dict.entity.SysDict;
import com.yourcompany.sales.modules.dict.entity.SysDictItem;
import com.yourcompany.sales.modules.dict.repository.SysDictItemRepository;
import com.yourcompany.sales.modules.dict.repository.SysDictRepository;
import com.yourcompany.sales.modules.system.entity.SysMenu;
import com.yourcompany.sales.modules.system.entity.SysRole;
import com.yourcompany.sales.modules.system.entity.SysRoleMenu;
import com.yourcompany.sales.modules.system.repository.SysMenuRepository;
import com.yourcompany.sales.modules.system.repository.SysRoleMenuRepository;
import com.yourcompany.sales.modules.system.repository.SysRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 后端 B 主数据初始化器
 *
 * 功能：
 *  1. 在 sys_menu/sys_role_menu 中追加 客户/商品/字典 三块菜单与按钮权限
 *     —— 补齐 SystemDataInitializer 里没有的部分
 *  2. 初始化 sys_dict / sys_dict_item 常用字典（客户等级、客户类型、跟进方式、商品单位等）
 *
 * 设计原则：
 *  - 全部用"先查后插"的 ensure 模式，重复启动不会脏数据
 *  - 仅追加，不修改 / 不删除其它模块的已有数据
 *  - 监听 ApplicationReadyEvent，确保所有 CommandLineRunner（含 A 的
 *    SystemDataInitializer）执行完毕、角色和根菜单已就位后再运行
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MasterDataInitializer {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_SALES = "SALES";
    private static final String ROLE_SALES_MANAGER = "SALES_MANAGER";

    private final SysMenuRepository menuRepository;
    private final SysRoleRepository roleRepository;
    private final SysRoleMenuRepository roleMenuRepository;
    private final SysDictRepository dictRepository;
    private final SysDictItemRepository dictItemRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onApplicationReady() {
        Map<String, SysMenu> menus = initBackendBMenus();
        bindMenusToRoles(menus);
        initDicts();
        log.info("后端 B 主数据初始化完成: 菜单 {} 项, 字典已就绪", menus.size());
    }

    // ============================================================
    // 一、菜单 + 权限码
    // ============================================================
    private Map<String, SysMenu> initBackendBMenus() {
        Map<String, SysMenu> menus = new LinkedHashMap<>();

        // 客户中心
        SysMenu customerRoot = ensureMenu(0L, "客户中心", "MENU", "/customer", null, null, 400);
        put(menus, ensureMenu(customerRoot.getId(), "客户列表", "MENU",   "/customer/list",       null, "customer:list",           410));
        put(menus, ensureMenu(customerRoot.getId(), "新增客户", "BUTTON", null,                   null, "customer:create",         411));
        put(menus, ensureMenu(customerRoot.getId(), "编辑客户", "BUTTON", null,                   null, "customer:update",         412));
        put(menus, ensureMenu(customerRoot.getId(), "客户状态", "BUTTON", null,                   null, "customer:status",         413));
        put(menus, ensureMenu(customerRoot.getId(), "删除客户", "BUTTON", null,                   null, "customer:delete",         414));
        put(menus, ensureMenu(customerRoot.getId(), "客户详情", "MENU",   "/customer/detail",     null, "customer:detail",         415));
        put(menus, ensureMenu(customerRoot.getId(), "联系人管理", "BUTTON", null,                 null, "customer:contact:manage", 416));
        put(menus, ensureMenu(customerRoot.getId(), "新增跟进", "BUTTON", null,                   null, "customer:followup:create",417));
        put(menus, ensureMenu(customerRoot.getId(), "跟进记录", "MENU",   "/customer/followups",  null, "customer:followup:list",  418));

        // 商品中心
        SysMenu productRoot = ensureMenu(0L, "商品中心", "MENU", "/product", null, null, 500);
        put(menus, ensureMenu(productRoot.getId(), "SPU 列表",   "MENU",   "/product/spu",         null, "product:spu:list",   510));
        put(menus, ensureMenu(productRoot.getId(), "新增 SPU",   "BUTTON", null,                   null, "product:spu:create", 511));
        put(menus, ensureMenu(productRoot.getId(), "编辑 SPU",   "BUTTON", null,                   null, "product:spu:update", 512));
        put(menus, ensureMenu(productRoot.getId(), "SKU 列表",   "MENU",   "/product/sku",         null, "product:sku:list",   520));
        put(menus, ensureMenu(productRoot.getId(), "新增 SKU",   "BUTTON", null,                   null, "product:sku:create", 521));
        put(menus, ensureMenu(productRoot.getId(), "编辑 SKU",   "BUTTON", null,                   null, "product:sku:update", 522));
        put(menus, ensureMenu(productRoot.getId(), "SKU 状态",   "BUTTON", null,                   null, "product:sku:status", 523));
        put(menus, ensureMenu(productRoot.getId(), "商品分类",   "MENU",   "/product/categories",  null, "product:category:list",   530));
        put(menus, ensureMenu(productRoot.getId(), "新增分类",   "BUTTON", null,                   null, "product:category:create", 531));
        put(menus, ensureMenu(productRoot.getId(), "编辑分类",   "BUTTON", null,                   null, "product:category:update", 532));

        // 字典管理（放在系统管理下）
        SysMenu systemRoot = findRootMenuByPath("/system");
        if (systemRoot != null) {
            put(menus, ensureMenu(systemRoot.getId(), "字典管理", "MENU",   "/system/dicts",  null, "system:dict:list",   50));
            put(menus, ensureMenu(systemRoot.getId(), "新增字典", "BUTTON", null,             null, "system:dict:create", 51));
            put(menus, ensureMenu(systemRoot.getId(), "编辑字典", "BUTTON", null,             null, "system:dict:update", 52));
            put(menus, ensureMenu(systemRoot.getId(), "字典项管理", "BUTTON", null,           null, "system:dict:item",   53));
        }

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

    private SysMenu findRootMenuByPath(String path) {
        return menuRepository.findByDeletedFlagOrderBySortNoAscIdAsc(0).stream()
                .filter(item -> item.getParentId() != null && item.getParentId() == 0L
                        && path.equals(item.getPath()))
                .findFirst()
                .orElse(null);
    }

    private void put(Map<String, SysMenu> map, SysMenu menu) {
        if (StringUtils.hasText(menu.getPermissionCode())) {
            map.put(menu.getPermissionCode(), menu);
        }
    }

    // ============================================================
    // 二、角色授权
    // ============================================================
    private void bindMenusToRoles(Map<String, SysMenu> menus) {
        // 销售员：客户的全部权限 + 商品只读
        bindRole(ROLE_SALES, Set.of(
                "customer:list", "customer:create", "customer:update", "customer:status",
                "customer:detail", "customer:contact:manage",
                "customer:followup:create", "customer:followup:list",
                "product:spu:list", "product:sku:list", "product:category:list"
        ), menus);

        // 销售经理：客户管理全部 + 商品维护 + 字典只读
        bindRole(ROLE_SALES_MANAGER, Set.of(
                "customer:list", "customer:create", "customer:update", "customer:status", "customer:delete",
                "customer:detail", "customer:contact:manage",
                "customer:followup:create", "customer:followup:list",
                "product:spu:list", "product:spu:create", "product:spu:update",
                "product:sku:list", "product:sku:create", "product:sku:update", "product:sku:status",
                "product:category:list", "product:category:create", "product:category:update"
        ), menus);

        // 管理员：全部
        bindRole(ROLE_ADMIN, menus.keySet(), menus);
    }

    private void bindRole(String roleCode, Set<String> permissionCodes, Map<String, SysMenu> menus) {
        SysRole role = roleRepository.findByRoleCodeAndDeletedFlag(roleCode, 0).orElse(null);
        if (role == null) {
            log.warn("角色不存在，跳过授权: {}", roleCode);
            return;
        }
        Set<Long> existing = new java.util.HashSet<>();
        for (SysRoleMenu rm : roleMenuRepository.findByRoleId(role.getId())) {
            existing.add(rm.getMenuId());
        }
        for (String code : permissionCodes) {
            SysMenu menu = menus.get(code);
            if (menu == null || existing.contains(menu.getId())) {
                continue;
            }
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getId());
            rm.setMenuId(menu.getId());
            roleMenuRepository.save(rm);
        }
    }

    // ============================================================
    // 三、字典初始数据
    // ============================================================
    private void initDicts() {
        List<DictSeed> seeds = new ArrayList<>();
        seeds.add(new DictSeed("CUSTOMER_LEVEL", "客户等级", List.of(
                new ItemSeed("A", "A 级 - 重点客户", 1),
                new ItemSeed("B", "B 级 - 一般客户", 2),
                new ItemSeed("C", "C 级 - 潜在客户", 3))));
        seeds.add(new DictSeed("CUSTOMER_TYPE", "客户类型", List.of(
                new ItemSeed("ENTERPRISE", "企业", 1),
                new ItemSeed("PERSONAL",   "个人", 2),
                new ItemSeed("CHANNEL",    "渠道", 3))));
        seeds.add(new DictSeed("CUSTOMER_SOURCE", "客户来源", List.of(
                new ItemSeed("ONLINE",     "网络推广", 1),
                new ItemSeed("REFERRAL",   "客户推荐", 2),
                new ItemSeed("EXHIBITION", "展会",     3))));
        seeds.add(new DictSeed("FOLLOW_TYPE", "跟进方式", List.of(
                new ItemSeed("PHONE",  "电话", 1),
                new ItemSeed("VISIT",  "拜访", 2),
                new ItemSeed("WECHAT", "微信", 3),
                new ItemSeed("EMAIL",  "邮件", 4))));
        seeds.add(new DictSeed("PRODUCT_UNIT", "商品计量单位", List.of(
                new ItemSeed("PCS", "件",   1),
                new ItemSeed("BOX", "箱",   2),
                new ItemSeed("KG",  "千克", 3))));

        for (DictSeed seed : seeds) {
            ensureDict(seed);
        }
    }

    private void ensureDict(DictSeed seed) {
        SysDict dict = dictRepository.findByDictCode(seed.code).orElseGet(() -> {
            SysDict d = new SysDict();
            d.setDictCode(seed.code);
            d.setDictName(seed.name);
            d.setStatus(1);
            d.setCreatedAt(LocalDateTime.now());
            return dictRepository.save(d);
        });

        for (ItemSeed item : seed.items) {
            boolean exists = dictItemRepository
                    .existsByDictCodeAndItemValueAndDeletedFlag(dict.getDictCode(), item.value, 0);
            if (exists) {
                continue;
            }
            SysDictItem di = new SysDictItem();
            di.setDictCode(dict.getDictCode());
            di.setItemValue(item.value);
            di.setItemLabel(item.label);
            di.setSortNo(item.sortNo);
            di.setStatus(1);
            di.setCreatedAt(LocalDateTime.now());
            dictItemRepository.save(di);
        }
    }

    // ---------- 内部小种子类，避免无谓的 Lombok 噪音 ----------
    private record DictSeed(String code, String name, List<ItemSeed> items) {}

    private record ItemSeed(String value, String label, int sortNo) {}
}
