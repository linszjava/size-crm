package com.size.auth.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.size.api.RemoteUserService;
import com.size.api.domain.LoginUser;
import com.size.api.domain.UserPermissionInfo;
import com.size.api.domain.UserRegisterDTO;
import com.size.auth.service.IAuthService;
import com.size.common.core.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private RemoteUserService remoteUserService;

    @Override
    public Result<?> login(String username, String password) {
        Result<LoginUser> userResult;
        try {
            userResult = remoteUserService.getUserInfo(username);
        } catch (Exception e) {
            return Result.fail("远程调用异常: " + e.getMessage());
        }

        if (userResult == null || userResult.getCode() != Result.SUCCESS || userResult.getData() == null) {
            return Result.fail("用户名或密码错误");
        }

        LoginUser loginUser = userResult.getData();

        if (loginUser.getStatus() != null && loginUser.getStatus() != 0) {
            return Result.fail(403, "账号已停用，请联系管理员");
        }

        String storedPassword = loginUser.getPassword();
        boolean passwordMatched = false;
        if (password != null && storedPassword != null && !storedPassword.isBlank()) {
            try {
                passwordMatched = BCrypt.checkpw(password, storedPassword);
            } catch (Exception e) {
                passwordMatched = false;
            }
        }
        if (!passwordMatched) {
            return Result.fail("用户名或密码错误");
        }

        if (loginUser.getRoles() == null || loginUser.getRoles().isEmpty()) {
            return Result.fail(403, "账号无可用角色，请联系管理员");
        }

        StpUtil.login(loginUser.getId());
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", StpUtil.getTokenValue());
        return Result.ok(tokenMap, "登录成功");
    }

    @Override
    public Result<?> register(UserRegisterDTO dto) {
        Result<Boolean> registerResult;
        try {
            registerResult = remoteUserService.register(dto);
        } catch (Exception e) {
            return Result.fail("注册失败: " + e.getMessage());
        }
        if (registerResult == null || registerResult.getCode() != Result.SUCCESS) {
            return Result.fail(registerResult == null ? "注册失败" : registerResult.getMsg());
        }
        return Result.ok(true, "注册成功");
    }

    @Override
    public Result<?> getUserInfo() {
        try {
            Long loginId = StpUtil.getLoginIdAsLong();
            Result<UserPermissionInfo> permissionResult = remoteUserService.getUserPermission(loginId);
            if (permissionResult == null || permissionResult.getCode() != Result.SUCCESS || permissionResult.getData() == null) {
                return Result.fail("获取用户信息失败");
            }
            UserPermissionInfo permissionInfo = permissionResult.getData();
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", permissionInfo.getUserId());
            userInfo.put("username", permissionInfo.getUsername());
            userInfo.put("realName", permissionInfo.getRealName());
            userInfo.put("avatar", permissionInfo.getAvatar());
            userInfo.put("desc", "管理大师");
            userInfo.put("roles", permissionInfo.getRoles());
            return Result.ok(userInfo);
        } catch (NotLoginException e) {
            return Result.fail(401, "Token 已失效，请重新登录");
        }
    }

    @Override
    public Result<?> getPermCode() {
        try {
            Long loginId = StpUtil.getLoginIdAsLong();
            Result<UserPermissionInfo> permissionResult = remoteUserService.getUserPermission(loginId);
            if (permissionResult == null || permissionResult.getCode() != Result.SUCCESS || permissionResult.getData() == null) {
                return Result.ok(Collections.emptyList());
            }
            return Result.ok(permissionResult.getData().getPermCodes());
        } catch (NotLoginException e) {
            return Result.fail(401, "Token 已失效，请重新登录");
        }
    }

    @Override
    public Result<?> getMenuList() {
        try {
            Long loginId = StpUtil.getLoginIdAsLong();
            Result<UserPermissionInfo> permissionResult = remoteUserService.getUserPermission(loginId);
            if (permissionResult == null || permissionResult.getCode() != Result.SUCCESS || permissionResult.getData() == null) {
                return Result.ok(Collections.emptyList());
            }
            return Result.ok(permissionResult.getData().getMenuRouteTree());
        } catch (NotLoginException e) {
            return Result.fail(401, "Token 已失效，请重新登录");
        }
    }

    @Override
    public Result<?> logout() {
        StpUtil.logout();
        return Result.ok("注销成功");
    }
}
