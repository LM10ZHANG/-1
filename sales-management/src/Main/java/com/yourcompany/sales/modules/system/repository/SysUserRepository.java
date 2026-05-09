package com.yourcompany.sales.modules.system.repository;

import com.yourcompany.sales.modules.system.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SysUserRepository extends JpaRepository<SysUser, Long>, JpaSpecificationExecutor<SysUser> {
    Optional<SysUser> findByUsernameAndDeletedFlag(String username, Integer deletedFlag);
    boolean existsByUsernameAndDeletedFlag(String username, Integer deletedFlag);
}
