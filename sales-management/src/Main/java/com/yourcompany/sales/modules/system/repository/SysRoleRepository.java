package com.yourcompany.sales.modules.system.repository;

import com.yourcompany.sales.modules.system.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SysRoleRepository extends JpaRepository<SysRole, Long> {
    Optional<SysRole> findByRoleCodeAndDeletedFlag(String roleCode, Integer deletedFlag);
    boolean existsByRoleCodeAndDeletedFlag(String roleCode, Integer deletedFlag);
    List<SysRole> findByIdInAndDeletedFlag(Collection<Long> ids, Integer deletedFlag);
}
