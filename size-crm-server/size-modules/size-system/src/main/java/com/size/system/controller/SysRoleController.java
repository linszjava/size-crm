package com.size.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.size.common.core.domain.Result;
import com.size.system.domain.SysRole;
import com.size.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 角色信息
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private ISysRoleService roleService;

    /**
     * 获取角色列表分页
     */
    @GetMapping("/page")
    public Result<Page<SysRole>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleKey) {

        Page<SysRole> pageParam = new Page<>(current, size);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(roleName != null && !roleName.isEmpty(), SysRole::getRoleName, roleName)
               .like(roleKey != null && !roleKey.isEmpty(), SysRole::getRoleKey, roleKey)
               .orderByAsc(SysRole::getRoleSort);

        return Result.ok(roleService.page(pageParam, wrapper));
    }

    /**
     * 获取全部角色列表
     */
    @GetMapping("/list")
    public Result<List<SysRole>> list() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysRole::getRoleSort);
        return Result.ok(roleService.list(wrapper));
    }

    /**
     * 获取角色详细信息
     */
    @GetMapping("/{id}")
    public Result<SysRole> getInfo(@PathVariable Long id) {
        return Result.ok(roleService.getById(id));
    }

    /**
     * 新增角色
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody SysRole role) {
        if (role.getTenantId() == null) {
            role.setTenantId(1L);
        }
        return Result.ok(roleService.save(role));
    }

    /**
     * 修改角色
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody SysRole role) {
        return Result.ok(roleService.updateById(role));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{ids}")
    public Result<Boolean> remove(@PathVariable Long[] ids) {
        return Result.ok(roleService.removeByIds(Arrays.asList(ids)));
    }
}
