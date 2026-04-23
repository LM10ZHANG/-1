package com.yourcompany.sales.modules.dict.service;

import com.yourcompany.sales.common.exception.BusinessException;
import com.yourcompany.sales.modules.dict.dto.DictItemRequest;
import com.yourcompany.sales.modules.dict.dto.DictItemVo;
import com.yourcompany.sales.modules.dict.dto.DictRequest;
import com.yourcompany.sales.modules.dict.entity.SysDict;
import com.yourcompany.sales.modules.dict.entity.SysDictItem;
import com.yourcompany.sales.modules.dict.repository.SysDictItemRepository;
import com.yourcompany.sales.modules.dict.repository.SysDictRepository;
import com.yourcompany.sales.utils.BeanCopyUtils;
import com.yourcompany.sales.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典业务服务
 *
 * 提供给前端的主接口：
 *  - GET /api/dicts/{code}     根据字典编码查询所有启用项，前端用于下拉框
 *
 * 其余 CRUD 接口仅限管理员使用。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictService {

    private final SysDictRepository dictRepository;
    private final SysDictItemRepository dictItemRepository;

    // ========== 字典主表 ==========
    @Transactional
    public SysDict createDict(DictRequest request) {
        if (dictRepository.existsByDictCode(request.getDictCode())) {
            throw BusinessException.alreadyExists("字典", "编码", request.getDictCode());
        }
        SysDict dict = new SysDict();
        BeanUtils.copyProperties(request, dict);
        dict.setCreatedBy(SecurityUtils.getCurrentUserId());
        dict.setCreatedAt(LocalDateTime.now());
        return dictRepository.save(dict);
    }

    @Transactional
    public SysDict updateDict(Long id, DictRequest request) {
        SysDict dict = dictRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("字典", id));
        dict.setDictName(request.getDictName());
        if (request.getStatus() != null) {
            dict.setStatus(request.getStatus());
        }
        dict.setRemark(request.getRemark());
        dict.setUpdatedBy(SecurityUtils.getCurrentUserId());
        dict.setUpdatedAt(LocalDateTime.now());
        return dictRepository.save(dict);
    }

    public List<SysDict> listDicts() {
        return dictRepository.findByDeletedFlagOrderByIdAsc(0);
    }

    // ========== 字典项 ==========

    /**
     * 根据字典编码查询字典项（只返回启用状态）
     * —— 前端下拉框/级联/状态映射统一调用该接口
     */
    public List<DictItemVo> listItemsByCode(String dictCode) {
        return dictItemRepository.findByDictCodeAndDeletedFlagOrderBySortNoAsc(dictCode, 0).stream()
                .filter(i -> i.getStatus() != null && i.getStatus() == 1)
                .map(i -> BeanCopyUtils.copyBean(i, DictItemVo.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public SysDictItem addItem(String dictCode, DictItemRequest request) {
        dictRepository.findByDictCode(dictCode)
                .orElseThrow(() -> BusinessException.notFound("字典", dictCode));
        if (dictItemRepository.existsByDictCodeAndItemValueAndDeletedFlag(dictCode, request.getItemValue(), 0)) {
            throw BusinessException.alreadyExists("字典项", "value", request.getItemValue());
        }
        SysDictItem item = new SysDictItem();
        BeanUtils.copyProperties(request, item);
        item.setDictCode(dictCode);
        item.setCreatedBy(SecurityUtils.getCurrentUserId());
        item.setCreatedAt(LocalDateTime.now());
        return dictItemRepository.save(item);
    }

    @Transactional
    public SysDictItem updateItem(Long itemId, DictItemRequest request) {
        SysDictItem item = dictItemRepository.findById(itemId)
                .orElseThrow(() -> BusinessException.notFound("字典项", itemId));
        item.setItemLabel(request.getItemLabel());
        item.setSortNo(request.getSortNo() == null ? 0 : request.getSortNo());
        if (request.getStatus() != null) {
            item.setStatus(request.getStatus());
        }
        item.setRemark(request.getRemark());
        item.setUpdatedBy(SecurityUtils.getCurrentUserId());
        item.setUpdatedAt(LocalDateTime.now());
        return dictItemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        SysDictItem item = dictItemRepository.findById(itemId)
                .orElseThrow(() -> BusinessException.notFound("字典项", itemId));
        item.setDeletedFlag(1);
        item.setUpdatedBy(SecurityUtils.getCurrentUserId());
        item.setUpdatedAt(LocalDateTime.now());
        dictItemRepository.save(item);
    }
}
