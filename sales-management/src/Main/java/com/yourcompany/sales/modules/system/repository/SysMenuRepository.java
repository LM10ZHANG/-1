package com.yourcompany.sales.modules.system.repository;

import com.yourcompany.sales.modules.system.entity.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SysMenuRepository extends JpaRepository<SysMenu, Long> {
    Optional<SysMenu> findByPermissionCodeAndDeletedFlag(String permissionCode, Integer deletedFlag);
    List<SysMenu> findByDeletedFlagOrderBySortNoAscIdAsc(Integer deletedFlag);
    List<SysMenu> findByIdInAndDeletedFlag(Collection<Long> ids, Integer deletedFlag);
}
