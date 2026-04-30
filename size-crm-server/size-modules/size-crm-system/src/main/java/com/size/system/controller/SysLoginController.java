package com.size.system.controller;

import com.size.api.domain.LoginUser;
import com.size.api.domain.UserPermissionInfo;
import com.size.api.domain.UserRegisterDTO;
import com.size.common.core.domain.Result;
import com.size.system.service.ISysLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微服务内部调用：系统登录提供者（HTTP 入口）
 */
@RestController
@RequestMapping("/system/user")
public class SysLoginController {

    @Autowired
    private ISysLoginService sysLoginService;

    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody UserRegisterDTO dto) {
        return sysLoginService.register(dto);
    }

    @GetMapping("/info/{username}")
    public Result<LoginUser> getUserInfo(@PathVariable("username") String username) {
        return sysLoginService.getUserInfo(username);
    }

    @GetMapping("/permission/{userId}")
    public Result<UserPermissionInfo> getUserPermission(@PathVariable("userId") Long userId) {
        return sysLoginService.getUserPermission(userId);
    }
}
