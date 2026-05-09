package com.yourcompany.sales.modules.system.repository;

import com.yourcompany.sales.modules.system.entity.SysRoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SysRoleMenuRepository extends JpaRepository<SysRoleMenu, Long> {
    List<SysRoleMenu> findByRoleId(Long roleId);
    List<SysRoleMenu> findByRoleIdIn(Collection<Long> roleIds);
    void deleteByRoleId(Long roleId);
}
