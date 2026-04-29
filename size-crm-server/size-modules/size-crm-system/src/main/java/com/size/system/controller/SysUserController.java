package com.size.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.size.common.core.domain.Result;
import com.size.system.domain.SysUser;
import com.size.system.model.dto.UserSaveDTO;
import com.size.system.model.vo.UserDetailVO;
import com.size.system.model.vo.UserPageVO;
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
    @SaCheckPermission("system:user:query")
    @GetMapping("/page")
    public Result<Page<UserPageVO>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phonenumber) {

        Page<SysUser> pageParam = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(username != null && !username.isEmpty(), SysUser::getUsername, username)
               .like(phonenumber != null && !phonenumber.isEmpty(), SysUser::getPhonenumber, phonenumber)
               .orderByDesc(SysUser::getCreateTime);

        return Result.ok(userService.pageUsersWithRoles(pageParam, wrapper));
    }

    /**
     * 获取用户列表（不分页）
     */
    @SaCheckPermission("system:user:query")
    @GetMapping("/list")
    public Result<List<SysUser>> list() {
        return Result.ok(userService.list());
    }

    /**
     * 根据用户编号获取详细信息
     */
    @SaCheckPermission("system:user:query")
    @GetMapping("/{id}")
    public Result<UserDetailVO> getInfo(@PathVariable Long id) {
        return Result.ok(userService.getUserDetail(id));
    }

    /**
     * 新增用户
     */
    @SaCheckPermission("system:user:add")
    @PostMapping
    public Result<Boolean> add(@RequestBody UserSaveDTO dto) {
        try {
            return Result.ok(userService.saveUser(dto));
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 修改用户
     */
    @SaCheckPermission("system:user:edit")
    @PutMapping
    public Result<Boolean> edit(@RequestBody UserSaveDTO dto) {
        try {
            return Result.ok(userService.updateUser(dto));
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @SaCheckPermission("system:user:delete")
    @DeleteMapping("/{ids}")
    public Result<Boolean> remove(@PathVariable Long[] ids) {
        return Result.ok(userService.removeUsers(Arrays.asList(ids)));
    }
}
