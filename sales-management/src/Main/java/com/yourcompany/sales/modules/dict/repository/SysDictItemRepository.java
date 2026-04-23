package com.yourcompany.sales.modules.dict.repository;

import com.yourcompany.sales.modules.dict.entity.SysDictItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 字典项数据访问接口
 */
@Repository
public interface SysDictItemRepository extends JpaRepository<SysDictItem, Long> {

    List<SysDictItem> findByDictCodeAndDeletedFlagOrderBySortNoAsc(String dictCode, Integer deletedFlag);

    boolean existsByDictCodeAndItemValueAndDeletedFlag(String dictCode, String itemValue, Integer deletedFlag);
}
