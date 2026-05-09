package com.yourcompany.sales.modules.system.service;

import com.yourcompany.sales.modules.system.dto.LoginRequest;
import com.yourcompany.sales.modules.system.dto.LoginResponse;
import com.yourcompany.sales.modules.system.dto.UserResponse;
import com.yourcompany.sales.modules.system.entity.SysRoleMenu;
import com.yourcompany.sales.modules.system.entity.SysUserRole;
import com.yourcompany.sales.modules.system.repository.SysRoleMenuRepository;
import com.yourcompany.sales.modules.system.repository.SysUserRoleRepository;
import com.yourcompany.sales.security.JwtTokenProvider;
import com.yourcompany.sales.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final SysUserRoleRepository userRoleRepository;
    private final SysRoleMenuRepository roleMenuRepository;
    private final MenuService menuService;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        LoginUser user = (LoginUser) authentication.getPrincipal();
        String token = jwtTokenProvider.createToken(user);
        var roleIds = userRoleRepository.findByUserId(user.getUserId()).stream().map(SysUserRole::getRoleId).toList();
        var menuIds = roleIds.isEmpty() ? java.util.List.<Long>of() : roleMenuRepository.findByRoleIdIn(roleIds).stream()
                .map(SysRoleMenu::getMenuId).distinct().toList();
        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpireSeconds())
                .user(UserResponse.builder()
                        .id(user.getUserId())
                        .username(user.getUsername())
                        .realName(user.getRealName())
                        .status(user.isEnabled() ? 1 : 0)
                        .roles(user.getRoleCodes())
                        .permissions(user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                                .filter(auth -> !auth.startsWith("ROLE_")).toList())
                        .build())
                .menus(menuService.treeByIds(menuIds))
                .build();
    }
}
