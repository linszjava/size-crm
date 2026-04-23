package com.size.system.controller;

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
    @GetMapping("/{id}")
    public Result<SysMenu> getInfo(@PathVariable Long id) {
        return Result.ok(menuService.getById(id));
    }

    /**
     * 新增菜单
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody SysMenu menu) {
        return Result.ok(menuService.save(menu));
    }

    /**
     * 修改菜单
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody SysMenu menu) {
        return Result.ok(menuService.updateById(menu));
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        // TODO: Validate if there are child menus before deleting
        return Result.ok(menuService.removeById(id));
    }
}
