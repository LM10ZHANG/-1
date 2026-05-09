package com.yourcompany.sales.modules.system.repository;

import com.yourcompany.sales.modules.system.entity.SysUserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SysUserRoleRepository extends JpaRepository<SysUserRole, Long> {
    List<SysUserRole> findByUserId(Long userId);
    List<SysUserRole> findByRoleIdIn(Collection<Long> roleIds);
    void deleteByUserId(Long userId);
}
