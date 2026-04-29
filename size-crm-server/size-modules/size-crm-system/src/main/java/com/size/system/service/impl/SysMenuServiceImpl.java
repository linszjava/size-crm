package com.size.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.size.system.domain.SysMenu;
import com.size.system.domain.SysRoleMenu;
import com.size.system.mapper.SysMenuMapper;
import com.size.system.mapper.SysRoleMenuMapper;
import com.size.system.service.ISysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SysMenu Service 实现类
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public void validateMenu(SysMenu menu, boolean isUpdate) {
        if (menu == null) {
            throw new IllegalArgumentException("菜单数据不能为空");
        }
        if (menu.getMenuType() == null || menu.getMenuType().isBlank()) {
            throw new IllegalArgumentException("菜单类型不能为空");
        }
        String menuType = menu.getMenuType().trim().toUpperCase();
        if (!"M".equals(menuType) && !"C".equals(menuType) && !"F".equals(menuType)) {
            throw new IllegalArgumentException("菜单类型仅支持 M/C/F");
        }
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (isUpdate && menu.getId() == null) {
            throw new IllegalArgumentException("菜单ID不能为空");
        }
        if (isUpdate && menu.getId() != null && menu.getId().equals(menu.getParentId())) {
            throw new IllegalArgumentException("上级菜单不能选择自己");
        }

        if ("F".equals(menuType)) {
            if (menu.getPerms() == null || menu.getPerms().isBlank()) {
                throw new IllegalArgumentException("按钮类型必须填写权限标识");
            }
            menu.setPath(null);
            menu.setComponent(null);
        } else {
            if (menu.getPath() == null || menu.getPath().isBlank()) {
                throw new IllegalArgumentException("目录或菜单必须填写路由地址");
            }
            if ("C".equals(menuType) && (menu.getComponent() == null || menu.getComponent().isBlank())) {
                throw new IllegalArgumentException("菜单类型必须填写组件路径");
            }
        }

        if (menu.getPath() != null && !menu.getPath().isBlank()) {
            LambdaQueryWrapper<SysMenu> pathWrapper = new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getParentId, menu.getParentId())
                    .eq(SysMenu::getPath, menu.getPath());
            if (isUpdate && menu.getId() != null) {
                pathWrapper.ne(SysMenu::getId, menu.getId());
            }
            if (count(pathWrapper) > 0) {
                throw new IllegalArgumentException("同级菜单路由地址不能重复");
            }
        }

        if (menu.getPerms() != null && !menu.getPerms().isBlank()) {
            LambdaQueryWrapper<SysMenu> permsWrapper = new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getPerms, menu.getPerms());
            if (isUpdate && menu.getId() != null) {
                permsWrapper.ne(SysMenu::getId, menu.getId());
            }
            if (count(permsWrapper) > 0) {
                throw new IllegalArgumentException("权限标识已存在");
            }
        }
    }

    @Override
    public boolean hasChildren(Long id) {
        return count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id)) > 0;
    }

    @Override
    public boolean isMenuAssignedToRole(Long id) {
        return sysRoleMenuMapper.selectCount(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMenuId, id)) > 0;
    }
}
