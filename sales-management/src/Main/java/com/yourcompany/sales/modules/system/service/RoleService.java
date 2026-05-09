package com.yourcompany.sales.modules.system.service;

import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.system.dto.RoleMenuRequest;
import com.yourcompany.sales.modules.system.dto.RoleRequest;
import com.yourcompany.sales.modules.system.dto.RoleResponse;
import com.yourcompany.sales.modules.system.entity.SysRole;
import com.yourcompany.sales.modules.system.entity.SysRoleMenu;
import com.yourcompany.sales.modules.system.repository.SysRoleMenuRepository;
import com.yourcompany.sales.modules.system.repository.SysRoleRepository;
import com.yourcompany.sales.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final SysRoleRepository roleRepository;
    private final SysRoleMenuRepository roleMenuRepository;

    public List<RoleResponse> list() {
        return roleRepository.findAll().stream()
                .filter(role -> role.getDeletedFlag() == null || role.getDeletedFlag() == 0)
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByRoleCodeAndDeletedFlag(request.getRoleCode(), 0)) {
            throw BusinessException.alreadyExists("角色", "编码", request.getRoleCode());
        }
        SysRole role = new SysRole();
        fill(role, request);
        role.setCreatedBy(SecurityUtils.getCurrentUserId());
        role.setCreatedAt(LocalDateTime.now());
        return toResponse(roleRepository.save(role));
    }

    @Transactional
    public RoleResponse update(Long id, RoleRequest request) {
        SysRole role = roleRepository.findById(id).orElseThrow(() -> BusinessException.notFound("角色", id));
        fill(role, request);
        role.setUpdatedBy(SecurityUtils.getCurrentUserId());
        role.setUpdatedAt(LocalDateTime.now());
        return toResponse(roleRepository.save(role));
    }

    @Transactional
    public void updateMenus(Long id, RoleMenuRequest request) {
        roleRepository.findById(id).orElseThrow(() -> BusinessException.notFound("角色", id));
        roleMenuRepository.deleteByRoleId(id);
        roleMenuRepository.flush();
        if (request.getMenuIds() == null) {
            return;
        }
        request.getMenuIds().stream().distinct().forEach(menuId -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(id);
            roleMenu.setMenuId(menuId);
            roleMenuRepository.save(roleMenu);
        });
    }

    private void fill(SysRole role, RoleRequest request) {
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        role.setRemark(request.getRemark());
    }

    public RoleResponse toResponse(SysRole role) {
        List<Long> menuIds = roleMenuRepository.findByRoleId(role.getId()).stream().map(SysRoleMenu::getMenuId).toList();
        return RoleResponse.builder()
                .id(role.getId())
                .roleCode(role.getRoleCode())
                .roleName(role.getRoleName())
                .status(role.getStatus())
                .remark(role.getRemark())
                .menuIds(menuIds)
                .build();
    }
}
