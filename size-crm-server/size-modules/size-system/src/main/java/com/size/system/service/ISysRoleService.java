package com.size.system.service;

import com.size.system.domain.SysRole;
import com.size.system.model.dto.RoleSaveDTO;
import com.size.system.model.vo.RoleDetailVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * SysRole Service 接口
 */
public interface ISysRoleService extends IService<SysRole> {
    boolean saveRole(RoleSaveDTO dto);

    boolean updateRole(RoleSaveDTO dto);

    RoleDetailVO getRoleDetail(Long id);

    boolean removeRoles(List<Long> ids);
}
