package com.size.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.exception.NotLoginException;
import com.size.api.domain.UserPermissionInfo;
import com.size.api.domain.UserRegisterDTO;
import com.size.common.core.domain.Result;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.crypto.digest.BCrypt;
import com.size.api.RemoteUserService;
import com.size.api.domain.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 认证授权中心 Controller
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private RemoteUserService remoteUserService;

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        // 调用 system 模块接口查询数据库
        Result<LoginUser> userResult = null;
        try {
            userResult = remoteUserService.getUserInfo(username);
        } catch (Exception e) {
            return Result.fail("远程调用异常: " + e.getMessage());
        }

        if (userResult == null || userResult.getCode() != 200 || userResult.getData() == null) {
            return Result.fail("用户名或密码错误");
        }

        LoginUser loginUser = userResult.getData();

        if (loginUser.getStatus() != null && loginUser.getStatus() != 0) {
            return Result.fail(403, "账号已停用，请联系管理员");
        }

        // 密码校验统一使用 BCrypt.checkpw（存库须为 BCrypt.hashpw 生成的哈希，见 system 用户保存/注册）
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

        // 角色被禁用或未分配有效角色时，禁止登录
        if (loginUser.getRoles() == null || loginUser.getRoles().isEmpty()) {
            return Result.fail(403, "账号无可用角色，请联系管理员");
        }

        // 登录成功，颁发 token
        StpUtil.login(loginUser.getId());
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", StpUtil.getTokenValue());
        return Result.ok(tokenMap, "登录成功");
    }

    /**
     * 注册接口
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody UserRegisterDTO dto) {
        Result<Boolean> registerResult;
        try {
            registerResult = remoteUserService.register(dto);
        } catch (Exception e) {
            return Result.fail("注册失败: " + e.getMessage());
        }
        if (registerResult == null || registerResult.getCode() != 200) {
            return Result.fail(registerResult == null ? "注册失败" : registerResult.getMsg());
        }
        return Result.ok(true, "注册成功");
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/getUserInfo")
    public Result<?> getUserInfo() {
        try {
            // 验证 token 是否有效，无效则抛出 NotLoginException
            Long loginId = StpUtil.getLoginIdAsLong();
            Result<UserPermissionInfo> permissionResult = remoteUserService.getUserPermission(loginId);
            if (permissionResult == null || permissionResult.getCode() != 200 || permissionResult.getData() == null) {
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

    /**
     * 获取权限码
     */
    @GetMapping("/getPermCode")
    public Result<?> getPermCode() {
        try {
            Long loginId = StpUtil.getLoginIdAsLong();
            Result<UserPermissionInfo> permissionResult = remoteUserService.getUserPermission(loginId);
            if (permissionResult == null || permissionResult.getCode() != 200 || permissionResult.getData() == null) {
                return Result.ok(Collections.emptyList());
            }
            return Result.ok(permissionResult.getData().getPermCodes());
        } catch (NotLoginException e) {
            return Result.fail(401, "Token 已失效，请重新登录");
        }
    }

    /**
     * 获取动态菜单
     */
    @GetMapping("/getMenuList")
    public Result<?> getMenuList() {
        try {
            Long loginId = StpUtil.getLoginIdAsLong();
            Result<UserPermissionInfo> permissionResult = remoteUserService.getUserPermission(loginId);
            if (permissionResult == null || permissionResult.getCode() != 200 || permissionResult.getData() == null) {
                return Result.ok(Collections.emptyList());
            }
            return Result.ok(permissionResult.getData().getMenuRouteTree());
        } catch (NotLoginException e) {
            return Result.fail(401, "Token 已失效，请重新登录");
        }
    }

    /**
     * 登出接口
     */
    @GetMapping("/logout")
    public Result<?> logout() {
        StpUtil.logout();
        return Result.ok("注销成功");
    }
}
