package com.size.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.size.system.domain.SysRole;
import com.size.system.domain.SysRoleMenu;
import com.size.system.mapper.SysRoleMapper;
import com.size.system.mapper.SysRoleMenuMapper;
import com.size.system.model.dto.RoleSaveDTO;
import com.size.system.model.vo.RoleDetailVO;
import com.size.system.service.ISysRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * SysRole Service 实现类
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRole(RoleSaveDTO dto) {
        Objects.requireNonNull(dto, "角色参数不能为空");
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        if (role.getTenantId() == null) {
            role.setTenantId(1L);
        }
        boolean saved = save(role);
        if (!saved) {
            return false;
        }
        replaceRoleMenus(role.getId(), dto.getMenuIds());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(RoleSaveDTO dto) {
        Objects.requireNonNull(dto, "角色参数不能为空");
        if (dto.getId() == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        boolean updated = updateById(role);
        if (!updated) {
            return false;
        }
        if (dto.getMenuIds() != null) {
            replaceRoleMenus(role.getId(), dto.getMenuIds());
        }
        return true;
    }

    @Override
    public RoleDetailVO getRoleDetail(Long id) {
        SysRole role = getById(id);
        if (role == null) {
            return null;
        }
        RoleDetailVO vo = new RoleDetailVO();
        BeanUtils.copyProperties(role, vo);
        List<Long> menuIds = sysRoleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id)
        ).stream().map(SysRoleMenu::getMenuId).filter(Objects::nonNull).collect(Collectors.toList());
        vo.setMenuIds(menuIds);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeRoles(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return true;
        }
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, ids));
        return removeByIds(ids);
    }

    private void replaceRoleMenus(Long roleId, List<Long> menuIds) {
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }
        List<SysRoleMenu> roleMenus = menuIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(menuId -> SysRoleMenu.builder().roleId(roleId).menuId(menuId).build())
                .collect(Collectors.toList());
        if (roleMenus.isEmpty()) {
            return;
        }
        roleMenus.forEach(sysRoleMenuMapper::insert);
    }
}
