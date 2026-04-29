package com.size.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.size.system.domain.SysUser;
import com.size.system.model.dto.UserSaveDTO;
import com.size.system.model.vo.UserDetailVO;
import com.size.system.model.vo.UserPageVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * SysUser Service 接口
 */
public interface ISysUserService extends IService<SysUser> {
    /**
     * 分页查询用户（含角色名称，批量填充）
     */
    Page<UserPageVO> pageUsersWithRoles(Page<SysUser> pageParam, LambdaQueryWrapper<SysUser> wrapper);

    boolean saveUser(UserSaveDTO dto);

    boolean updateUser(UserSaveDTO dto);

    UserDetailVO getUserDetail(Long id);

    boolean removeUsers(List<Long> ids);
}
