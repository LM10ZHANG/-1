package com.example.controller;

import com.example.common.Result;
import com.example.entity.Choice;
import com.example.entity.Course;
import com.example.service.ChoiceService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 选课信息前端请求的接口入口
 */
@RestController
@RequestMapping("/choice")
public class ChoiceController {

    @Resource
    private ChoiceService choiceService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Course course) {
        choiceService.add(course);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping("/update")
    public Result update(@RequestBody Choice choice) {
        choiceService.updateById(choice);
        return Result.success();
    }

    /**
     * 根据id删除
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        choiceService.deleteById(id);
        return Result.success();
    }


    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Choice choice,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "5") Integer pageSize) {
        PageInfo<Choice> pageInfo = choiceService.selectPage(choice, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Choice> list = choiceService.selectAll();
        return Result.success(list);
    }
}
