package com.size.system.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.size.system.domain.SysRole;
import com.size.system.domain.SysUser;
import com.size.system.domain.SysUserRole;
import com.size.system.mapper.SysRoleMapper;
import com.size.system.mapper.SysUserMapper;
import com.size.system.mapper.SysUserRoleMapper;
import com.size.system.model.dto.UserSaveDTO;
import com.size.system.model.vo.UserDetailVO;
import com.size.system.model.vo.UserPageVO;
import com.size.system.service.ISysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * SysUser Service 实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public Page<UserPageVO> pageUsersWithRoles(Page<SysUser> pageParam, LambdaQueryWrapper<SysUser> wrapper) {
        Page<SysUser> userPage = page(pageParam, wrapper);
        List<SysUser> records = userPage.getRecords();
        if (records.isEmpty()) {
            Page<UserPageVO> empty = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
            empty.setRecords(Collections.emptyList());
            return empty;
        }

        List<Long> userIds = records.stream().map(SysUser::getId).filter(Objects::nonNull).collect(Collectors.toList());
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, userIds));

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> roleIdToName = new HashMap<>();
        if (!roleIds.isEmpty()) {
            List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIds);
            for (SysRole r : roles) {
                if (r.getId() != null && r.getRoleName() != null) {
                    roleIdToName.put(r.getId(), r.getRoleName());
                }
            }
        }

        Map<Long, List<String>> userIdToRoleNames = new HashMap<>();
        for (SysUserRole ur : userRoles) {
            if (ur.getUserId() == null || ur.getRoleId() == null) {
                continue;
            }
            String name = roleIdToName.get(ur.getRoleId());
            if (name != null) {
                userIdToRoleNames.computeIfAbsent(ur.getUserId(), k -> new ArrayList<>()).add(name);
            }
        }
        userIdToRoleNames.forEach((uid, names) -> names.sort(Comparator.naturalOrder()));

        List<UserPageVO> vos = new ArrayList<>(records.size());
        for (SysUser u : records) {
            UserPageVO vo = new UserPageVO();
            BeanUtils.copyProperties(u, vo);
            vo.setRoleNames(new ArrayList<>(userIdToRoleNames.getOrDefault(u.getId(), Collections.emptyList())));
            vos.add(vo);
        }

        Page<UserPageVO> result = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        result.setRecords(vos);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveUser(UserSaveDTO dto) {
        Objects.requireNonNull(dto, "用户参数不能为空");
        validateUsername(dto.getUsername(), null);
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        if (user.getTenantId() == null) {
            user.setTenantId(1L);
        }
        boolean saved = save(user);
        if (!saved) {
            return false;
        }
        replaceUserRoles(user.getId(), dto.getRoleIds());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(UserSaveDTO dto) {
        Objects.requireNonNull(dto, "用户参数不能为空");
        if (dto.getId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        validateUsername(dto.getUsername(), dto.getId());
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(BCrypt.hashpw(dto.getPassword()));
        }
        boolean updated = updateById(user);
        if (!updated) {
            return false;
        }
        if (dto.getRoleIds() != null) {
            replaceUserRoles(user.getId(), dto.getRoleIds());
        }
        return true;
    }

    @Override
    public UserDetailVO getUserDetail(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            return null;
        }
        UserDetailVO vo = new UserDetailVO();
        BeanUtils.copyProperties(user, vo);
        List<Long> roleIds = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id)
        ).stream().map(SysUserRole::getRoleId).filter(Objects::nonNull).collect(Collectors.toList());
        vo.setRoleIds(roleIds);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return true;
        }
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, ids));
        return removeByIds(ids);
    }

    private void replaceUserRoles(Long userId, List<Long> roleIds) {
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        roleIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(roleId -> SysUserRole.builder().userId(userId).roleId(roleId).build())
                .forEach(sysUserRoleMapper::insert);
    }

    private void validateUsername(String username, Long currentUserId) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("用户账号不能为空");
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username);
        if (currentUserId != null) {
            wrapper.ne(SysUser::getId, currentUserId);
        }
        if (count(wrapper) > 0) {
            throw new IllegalArgumentException("用户账号已存在，请更换");
        }
    }
}
