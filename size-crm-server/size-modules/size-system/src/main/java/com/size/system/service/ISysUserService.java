package com.size.system.service;

import com.size.system.domain.SysUser;
import com.size.system.model.dto.UserSaveDTO;
import com.size.system.model.vo.UserDetailVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * SysUser Service 接口
 */
public interface ISysUserService extends IService<SysUser> {
    boolean saveUser(UserSaveDTO dto);

    boolean updateUser(UserSaveDTO dto);

    UserDetailVO getUserDetail(Long id);

    boolean removeUsers(List<Long> ids);
}
