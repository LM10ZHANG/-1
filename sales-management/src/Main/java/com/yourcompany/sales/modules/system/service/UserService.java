package com.yourcompany.sales.modules.system.service;

import com.yourcompany.sales.common.dto.PageResponse;
import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.system.dto.UserRequest;
import com.yourcompany.sales.modules.system.dto.UserResponse;
import com.yourcompany.sales.modules.system.entity.SysRole;
import com.yourcompany.sales.modules.system.entity.SysUser;
import com.yourcompany.sales.modules.system.entity.SysUserRole;
import com.yourcompany.sales.modules.system.repository.SysRoleRepository;
import com.yourcompany.sales.modules.system.repository.SysUserRepository;
import com.yourcompany.sales.modules.system.repository.SysUserRoleRepository;
import com.yourcompany.sales.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final SysUserRoleRepository userRoleRepository;
    private final SystemUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public PageResponse<UserResponse> page(Integer pageNum, Integer pageSize) {
        int pn = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int ps = pageSize == null || pageSize < 1 ? 10 : pageSize;
        var page = userRepository.findAll((root, query, cb) -> cb.equal(root.get("deletedFlag"), 0),
                PageRequest.of(pn - 1, ps, Sort.by(Sort.Direction.DESC, "id")));
        return PageResponse.of(page.getContent().stream().map(this::toResponse).toList(), page.getTotalElements(), pn, ps);
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByUsernameAndDeletedFlag(request.getUsername(), 0)) {
            throw BusinessException.alreadyExists("用户", "用户名", request.getUsername());
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        SysUser user = new SysUser();
        fill(user, request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedBy(SecurityUtils.getCurrentUserId());
        user.setCreatedAt(LocalDateTime.now());
        SysUser saved = userRepository.save(user);
        saveUserRoles(saved.getId(), request.getRoleIds());
        return toResponse(saved);
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        SysUser user = userRepository.findById(id).orElseThrow(() -> BusinessException.notFound("用户", id));
        fill(user, request);
        if (StringUtils.hasText(request.getPassword())) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        user.setUpdatedBy(SecurityUtils.getCurrentUserId());
        user.setUpdatedAt(LocalDateTime.now());
        SysUser saved = userRepository.save(user);
        saveUserRoles(id, request.getRoleIds());
        return toResponse(saved);
    }

    @Transactional
    public void changeStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值仅允许 0 或 1");
        }
        SysUser user = userRepository.findById(id).orElseThrow(() -> BusinessException.notFound("用户", id));
        user.setStatus(status);
        user.setUpdatedBy(SecurityUtils.getCurrentUserId());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private void fill(SysUser user, UserRequest request) {
        user.setUsername(request.getUsername());
        user.setRealName(request.getRealName());
        user.setMobile(request.getMobile());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus() == null ? 1 : request.getStatus());
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        userRoleRepository.deleteByUserId(userId);
        userRoleRepository.flush();
        if (roleIds == null) {
            return;
        }
        roleIds.stream().distinct().forEach(roleId -> {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleRepository.save(userRole);
        });
    }

    public UserResponse toResponse(SysUser user) {
        LoginUserDetail detail = loadDetail(user);
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .mobile(user.getMobile())
                .email(user.getEmail())
                .status(user.getStatus())
                .roles(detail.roles())
                .permissions(detail.permissions())
                .build();
    }

    private LoginUserDetail loadDetail(SysUser user) {
        var roleIds = userRoleRepository.findByUserId(user.getId()).stream().map(SysUserRole::getRoleId).toList();
        List<SysRole> roles = roleIds.isEmpty() ? List.of() : roleRepository.findByIdInAndDeletedFlag(roleIds, 0);
        var loginUser = (com.yourcompany.sales.security.LoginUser) userDetailsService.loadUserByUsername(user.getUsername());
        return new LoginUserDetail(
                roles.stream().map(SysRole::getRoleCode).toList(),
                loginUser.getAuthorities().stream().map(auth -> auth.getAuthority()).filter(v -> !v.startsWith("ROLE_")).toList());
    }

    private record LoginUserDetail(List<String> roles, List<String> permissions) {
    }
}
