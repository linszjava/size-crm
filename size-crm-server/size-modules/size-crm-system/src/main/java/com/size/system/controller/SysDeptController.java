package com.size.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.size.common.core.domain.Result;
import com.size.system.domain.SysDept;
import com.size.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 部门信息
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    @Autowired
    private ISysDeptService deptService;

    /**
     * 获取部门列表(树形基础列表)
     */
    @GetMapping("/list")
    public Result<List<SysDept>> list(
            @RequestParam(required = false) String deptName,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(deptName != null && !deptName.isEmpty(), SysDept::getDeptName, deptName)
               .eq(status != null, SysDept::getStatus, status)
               .orderByAsc(SysDept::getOrderNum);

        return Result.ok(deptService.list(wrapper));
    }

    /**
     * 获取部门详细信息
     */
    @GetMapping("/{id}")
    public Result<SysDept> getInfo(@PathVariable Long id) {
        return Result.ok(deptService.getById(id));
    }

    /**
     * 新增部门
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody SysDept dept) {
        if (dept.getTenantId() == null) {
            dept.setTenantId(1L);
        }
        return Result.ok(deptService.save(dept));
    }

    /**
     * 修改部门
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody SysDept dept) {
        return Result.ok(deptService.updateById(dept));
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        // TODO: Validate if there are child departments or users before deleting
        return Result.ok(deptService.removeById(id));
    }
}
