package com.yourcompany.sales.modules.system.service;

import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.system.dto.MenuRequest;
import com.yourcompany.sales.modules.system.dto.MenuResponse;
import com.yourcompany.sales.modules.system.entity.SysMenu;
import com.yourcompany.sales.modules.system.repository.SysMenuRepository;
import com.yourcompany.sales.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final SysMenuRepository menuRepository;

    public List<MenuResponse> tree() {
        return buildTree(menuRepository.findByDeletedFlagOrderBySortNoAscIdAsc(0));
    }

    public List<MenuResponse> treeByIds(List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return List.of();
        }
        return buildTree(menuRepository.findByIdInAndDeletedFlag(menuIds, 0));
    }

    @Transactional
    public MenuResponse create(MenuRequest request) {
        SysMenu menu = new SysMenu();
        fill(menu, request);
        menu.setCreatedBy(SecurityUtils.getCurrentUserId());
        menu.setCreatedAt(LocalDateTime.now());
        return toResponse(menuRepository.save(menu));
    }

    @Transactional
    public MenuResponse update(Long id, MenuRequest request) {
        SysMenu menu = menuRepository.findById(id).orElseThrow(() -> BusinessException.notFound("菜单", id));
        fill(menu, request);
        menu.setUpdatedBy(SecurityUtils.getCurrentUserId());
        menu.setUpdatedAt(LocalDateTime.now());
        return toResponse(menuRepository.save(menu));
    }

    @Transactional
    public void delete(Long id) {
        SysMenu menu = menuRepository.findById(id).orElseThrow(() -> BusinessException.notFound("菜单", id));
        menu.setDeletedFlag(1);
        menu.setUpdatedBy(SecurityUtils.getCurrentUserId());
        menu.setUpdatedAt(LocalDateTime.now());
        menuRepository.save(menu);
    }

    private void fill(SysMenu menu, MenuRequest request) {
        menu.setParentId(request.getParentId() == null ? 0L : request.getParentId());
        menu.setMenuName(request.getMenuName());
        menu.setMenuType(request.getMenuType());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setPermissionCode(request.getPermissionCode());
        menu.setSortNo(request.getSortNo() == null ? 0 : request.getSortNo());
        menu.setStatus(request.getStatus() == null ? 1 : request.getStatus());
    }

    public List<MenuResponse> buildTree(List<SysMenu> menus) {
        Map<Long, MenuResponse> nodeMap = new LinkedHashMap<>();
        menus.stream().sorted(Comparator.comparing(SysMenu::getSortNo).thenComparing(SysMenu::getId))
                .forEach(menu -> nodeMap.put(menu.getId(), toResponse(menu)));
        nodeMap.values().forEach(node -> {
            if (node.getParentId() != null && node.getParentId() != 0 && nodeMap.containsKey(node.getParentId())) {
                nodeMap.get(node.getParentId()).getChildren().add(node);
            }
        });
        return nodeMap.values().stream()
                .filter(node -> node.getParentId() == null || node.getParentId() == 0 || !nodeMap.containsKey(node.getParentId()))
                .toList();
    }

    public MenuResponse toResponse(SysMenu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .parentId(menu.getParentId())
                .menuName(menu.getMenuName())
                .menuType(menu.getMenuType())
                .path(menu.getPath())
                .component(menu.getComponent())
                .permissionCode(menu.getPermissionCode())
                .sortNo(menu.getSortNo())
                .status(menu.getStatus())
                .build();
    }
}
