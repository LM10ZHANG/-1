package com.yourcompany.sales.modules.dict.controller;

import com.yourcompany.sales.common.dto.ApiResponse;
import com.yourcompany.sales.modules.dict.dto.DictItemRequest;
import com.yourcompany.sales.modules.dict.dto.DictItemVo;
import com.yourcompany.sales.modules.dict.dto.DictRequest;
import com.yourcompany.sales.modules.dict.entity.SysDict;
import com.yourcompany.sales.modules.dict.entity.SysDictItem;
import com.yourcompany.sales.modules.dict.service.DictService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典控制器
 *
 * 主要路由：
 *  - GET    /api/dicts                      字典列表（管理员用）
 *  - POST   /api/dicts                      新增字典
 *  - PUT    /api/dicts/{id}                 更新字典
 *  - GET    /api/dicts/{code}/items         根据编码查询启用项（前端下拉框使用，无登录要求）
 *  - POST   /api/dicts/{code}/items         新增字典项
 *  - PUT    /api/dicts/items/{itemId}       更新字典项
 *  - DELETE /api/dicts/items/{itemId}       删除字典项
 */
@RestController
@RequestMapping("/api/dicts")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @GetMapping
    public ApiResponse<List<SysDict>> listDicts() {
        return ApiResponse.success(dictService.listDicts());
    }

    @PostMapping
    public ApiResponse<SysDict> createDict(@Valid @RequestBody DictRequest request) {
        return ApiResponse.success(dictService.createDict(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<SysDict> updateDict(@PathVariable Long id,
                                           @Valid @RequestBody DictRequest request) {
        return ApiResponse.success(dictService.updateDict(id, request));
    }

    /**
     * 前端下拉字典项接口
     */
    @GetMapping("/{code}/items")
    public ApiResponse<List<DictItemVo>> listItems(@PathVariable("code") String dictCode) {
        return ApiResponse.success(dictService.listItemsByCode(dictCode));
    }

    @PostMapping("/{code}/items")
    public ApiResponse<SysDictItem> addItem(@PathVariable("code") String dictCode,
                                            @Valid @RequestBody DictItemRequest request) {
        return ApiResponse.success(dictService.addItem(dictCode, request));
    }

    @PutMapping("/items/{itemId}")
    public ApiResponse<SysDictItem> updateItem(@PathVariable Long itemId,
                                               @Valid @RequestBody DictItemRequest request) {
        return ApiResponse.success(dictService.updateItem(itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<Void> deleteItem(@PathVariable Long itemId) {
        dictService.deleteItem(itemId);
        return ApiResponse.success();
    }
}
