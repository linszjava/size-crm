package com.size.system.service;

import com.size.api.domain.LoginUser;
import com.size.api.domain.UserPermissionInfo;
import com.size.api.domain.UserRegisterDTO;
import com.size.common.core.domain.Result;

/**
 * 系统登录 / 用户信息提供（供 Feign 与内部注册）
 */
public interface ISysLoginService {

    Result<Boolean> register(UserRegisterDTO dto);

    Result<LoginUser> getUserInfo(String username);

    Result<UserPermissionInfo> getUserPermission(Long userId);
}
