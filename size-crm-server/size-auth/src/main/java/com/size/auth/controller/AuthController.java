package com.size.auth.controller;

import com.size.api.domain.UserRegisterDTO;
import com.size.auth.service.IAuthService;
import com.size.common.core.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证授权中心 HTTP 入口
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> body) {
        return authService.login(body.get("username"), body.get("password"));
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody UserRegisterDTO dto) {
        return authService.register(dto);
    }

    @GetMapping("/getUserInfo")
    public Result<?> getUserInfo() {
        return authService.getUserInfo();
    }

    @GetMapping("/getPermCode")
    public Result<?> getPermCode() {
        return authService.getPermCode();
    }

    @GetMapping("/getMenuList")
    public Result<?> getMenuList() {
        return authService.getMenuList();
    }

    @GetMapping("/logout")
    public Result<?> logout() {
        return authService.logout();
    }
}
