package com.yourcompany.sales.modules.dict.repository;

import com.yourcompany.sales.modules.dict.entity.SysDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 字典主表数据访问接口
 */
@Repository
public interface SysDictRepository extends JpaRepository<SysDict, Long> {

    Optional<SysDict> findByDictCode(String dictCode);

    boolean existsByDictCode(String dictCode);

    List<SysDict> findByDeletedFlagOrderByIdAsc(Integer deletedFlag);
}
