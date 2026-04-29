package com.size.system.service;

import com.size.system.domain.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * SysMenu Service 接口
 */
public interface ISysMenuService extends IService<SysMenu> {
    /**
     * 校验菜单参数
     */
    void validateMenu(SysMenu menu, boolean isUpdate);

    /**
     * 是否存在子菜单
     */
    boolean hasChildren(Long id);

    /**
     * 是否被角色绑定
     */
    boolean isMenuAssignedToRole(Long id);
}
