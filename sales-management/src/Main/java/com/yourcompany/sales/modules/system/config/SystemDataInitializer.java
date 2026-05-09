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

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SystemDataInitializer implements CommandLineRunner {

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final SysMenuRepository menuRepository;
    private final SysUserRoleRepository userRoleRepository;
    private final SysRoleMenuRepository roleMenuRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        SysRole adminRole = roleRepository.findByRoleCodeAndDeletedFlag("ADMIN", 0).orElseGet(() -> {
            SysRole role = new SysRole();
            role.setRoleCode("ADMIN");
            role.setRoleName("系统管理员");
            role.setStatus(1);
            role.setRemark("系统初始化角色");
            role.setCreatedAt(LocalDateTime.now());
            return roleRepository.save(role);
        });

        SysUser admin = userRepository.findByUsernameAndDeletedFlag("admin", 0).orElseGet(() -> {
            SysUser user = new SysUser();
            user.setUsername("admin");
            user.setRealName("系统管理员");
            user.setPasswordHash(passwordEncoder.encode("123456"));
            user.setStatus(1);
            user.setCreatedAt(LocalDateTime.now());
            return userRepository.save(user);
        });

        if (userRoleRepository.findByUserId(admin.getId()).stream().noneMatch(item -> item.getRoleId().equals(adminRole.getId()))) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(admin.getId());
            userRole.setRoleId(adminRole.getId());
            userRoleRepository.save(userRole);
        }

        List<SysMenu> menus = List.of(
                menu("系统管理", "MENU", "/system", null, null, 1),
                menu("用户管理", "MENU", "/system/users", null, "system:user:list", 10),
                menu("新增用户", "BUTTON", null, null, "system:user:create", 11),
                menu("编辑用户", "BUTTON", null, null, "system:user:update", 12),
                menu("用户状态", "BUTTON", null, null, "system:user:status", 13),
                menu("角色管理", "MENU", "/system/roles", null, "system:role:list", 20),
                menu("新增角色", "BUTTON", null, null, "system:role:create", 21),
                menu("编辑角色", "BUTTON", null, null, "system:role:update", 22),
                menu("角色授权", "BUTTON", null, null, "system:role:menus", 23),
                menu("菜单管理", "MENU", "/system/menus", null, "system:menu:list", 30),
                menu("新增菜单", "BUTTON", null, null, "system:menu:create", 31),
                menu("编辑菜单", "BUTTON", null, null, "system:menu:update", 32),
                menu("删除菜单", "BUTTON", null, null, "system:menu:delete", 33),
                menu("操作日志", "MENU", "/system/logs", null, "system:log:list", 40)
        );

        for (SysMenu menu : menus) {
            SysMenu saved = menuRepository.findByPermissionCodeAndDeletedFlag(menu.getPermissionCode(), 0)
                    .orElseGet(() -> menuRepository.save(menu));
            if (roleMenuRepository.findByRoleId(adminRole.getId()).stream().noneMatch(item -> item.getMenuId().equals(saved.getId()))) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(adminRole.getId());
                roleMenu.setMenuId(saved.getId());
                roleMenuRepository.save(roleMenu);
            }
        }
    }

    private SysMenu menu(String name, String type, String path, String component, String permission, Integer sortNo) {
        SysMenu menu = new SysMenu();
        menu.setParentId(0L);
        menu.setMenuName(name);
        menu.setMenuType(type);
        menu.setPath(path);
        menu.setComponent(component);
        menu.setPermissionCode(permission);
        menu.setSortNo(sortNo);
        menu.setStatus(1);
        menu.setCreatedAt(LocalDateTime.now());
        return menu;
    }
}
