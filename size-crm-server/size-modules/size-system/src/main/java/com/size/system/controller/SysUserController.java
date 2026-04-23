package com.size.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.size.common.core.domain.Result;
import com.size.system.domain.SysUser;
import com.size.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 用户信息
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    /**
     * 获取用户列表
     */
    @GetMapping("/page")
    public Result<Page<SysUser>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phonenumber) {

        Page<SysUser> pageParam = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(username != null && !username.isEmpty(), SysUser::getUsername, username)
               .like(phonenumber != null && !phonenumber.isEmpty(), SysUser::getPhonenumber, phonenumber)
               .orderByDesc(SysUser::getCreateTime);

        return Result.ok(userService.page(pageParam, wrapper));
    }

    /**
     * 获取用户列表（不分页）
     */
    @GetMapping("/list")
    public Result<List<SysUser>> list() {
        return Result.ok(userService.list());
    }

    /**
     * 根据用户编号获取详细信息
     */
    @GetMapping("/{id}")
    public Result<SysUser> getInfo(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody SysUser user) {
        if (user.getTenantId() == null) {
            user.setTenantId(1L);
        }
        return Result.ok(userService.save(user));
    }

    /**
     * 修改用户
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody SysUser user) {
        return Result.ok(userService.updateById(user));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{ids}")
    public Result<Boolean> remove(@PathVariable Long[] ids) {
        return Result.ok(userService.removeByIds(Arrays.asList(ids)));
    }
}
