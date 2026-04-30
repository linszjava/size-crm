package com.size.auth.service;

import com.size.api.domain.UserRegisterDTO;
import com.size.common.core.domain.Result;

import java.util.Map;

/**
 * 认证中心：登录、注册、会话相关查询
 */
public interface IAuthService {

    Result<?> login(String username, String password);

    Result<?> register(UserRegisterDTO dto);

    Result<?> getUserInfo();

    Result<?> getPermCode();

    Result<?> getMenuList();

    Result<?> logout();
}
