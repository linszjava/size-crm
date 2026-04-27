package com.size.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.size.common.core.domain.Result;
import com.size.system.domain.SysMenu;
import com.size.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单信息
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @SaCheckPermission("system:menu:query")
    @GetMapping("/list")
    public Result<List<SysMenu>> list(
            @RequestParam(required = false) String menuName,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(menuName != null && !menuName.isEmpty(), SysMenu::getMenuName, menuName)
               .eq(status != null, SysMenu::getStatus, status)
               .orderByAsc(SysMenu::getParentId)
               .orderByAsc(SysMenu::getOrderNum);

        return Result.ok(menuService.list(wrapper));
    }

    /**
     * 获取菜单详细信息
     */
    @SaCheckPermission("system:menu:query")
    @GetMapping("/{id}")
    public Result<SysMenu> getInfo(@PathVariable Long id) {
        return Result.ok(menuService.getById(id));
    }

    /**
     * 新增菜单
     */
    @SaCheckPermission("system:menu:add")
    @PostMapping
    public Result<Boolean> add(@RequestBody SysMenu menu) {
        try {
            menuService.validateMenu(menu, false);
            return Result.ok(menuService.save(menu));
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 修改菜单
     */
    @SaCheckPermission("system:menu:edit")
    @PutMapping
    public Result<Boolean> edit(@RequestBody SysMenu menu) {
        try {
            menuService.validateMenu(menu, true);
            return Result.ok(menuService.updateById(menu));
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 删除菜单
     */
    @SaCheckPermission("system:menu:delete")
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        if (menuService.hasChildren(id)) {
            return Result.fail("存在子菜单，无法删除");
        }
        if (menuService.isMenuAssignedToRole(id)) {
            return Result.fail("菜单已分配给角色，无法删除");
        }
        return Result.ok(menuService.removeById(id));
    }
}
