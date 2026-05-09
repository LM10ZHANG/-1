package com.yourcompany.sales.modules.system.service;

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
import com.yourcompany.sales.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemUserDetailsService implements UserDetailsService {

    private final SysUserRepository userRepository;
    private final SysUserRoleRepository userRoleRepository;
    private final SysRoleRepository roleRepository;
    private final SysRoleMenuRepository roleMenuRepository;
    private final SysMenuRepository menuRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userRepository.findByUsernameAndDeletedFlag(username, 0)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        List<Long> roleIds = userRoleRepository.findByUserId(user.getId()).stream()
                .map(SysUserRole::getRoleId)
                .toList();
        List<SysRole> roles = roleIds.isEmpty() ? List.of() : roleRepository.findByIdInAndDeletedFlag(roleIds, 0);
        List<String> roleCodes = roles.stream()
                .filter(role -> role.getStatus() != null && role.getStatus() == 1)
                .map(SysRole::getRoleCode)
                .toList();
        List<Long> activeRoleIds = roles.stream()
                .filter(role -> role.getStatus() != null && role.getStatus() == 1)
                .map(SysRole::getId)
                .toList();
        List<Long> menuIds = activeRoleIds.isEmpty() ? List.of() : roleMenuRepository.findByRoleIdIn(activeRoleIds).stream()
                .map(SysRoleMenu::getMenuId)
                .distinct()
                .toList();
        List<SysMenu> menus = menuIds.isEmpty() ? List.of() : menuRepository.findByIdInAndDeletedFlag(menuIds, 0);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roleCodes.forEach(code -> authorities.add(new SimpleGrantedAuthority("ROLE_" + code)));
        menus.stream()
                .filter(menu -> menu.getStatus() != null && menu.getStatus() == 1)
                .map(SysMenu::getPermissionCode)
                .filter(StringUtils::hasText)
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);
        return new LoginUser(user.getId(), user.getUsername(), user.getRealName(), user.getPasswordHash(),
                user.getStatus(), roleCodes, authorities.stream().collect(Collectors.toList()));
    }
}
