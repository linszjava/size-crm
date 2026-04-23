package com.size.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.exception.NotLoginException;
import com.size.common.core.domain.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        
        System.out.println("====== LOGIN DEBUG ======");
        System.out.println("Attempting login for: " + username);

        // 调用 system 模块接口查询数据库
        Result<LoginUser> userResult = null;
        try {
            userResult = remoteUserService.getUserInfo(username);
            System.out.println("remoteUserService result: " + userResult);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("远程调用异常: " + e.getMessage());
        }

        if (userResult == null || userResult.getCode() != 200 || userResult.getData() == null) {
            System.out.println("Login Failed: userResult invalid");
            return Result.fail("用户名或密码错误");
        }
        
        LoginUser loginUser = userResult.getData();
        System.out.println("LoginUser retrieved: " + loginUser.getUsername());
        System.out.println("Password from DB: " + loginUser.getPassword());
        
        // 校验密码
        if (!BCrypt.checkpw(password, loginUser.getPassword())) {
            System.out.println("Login Failed: BCrypt mismatch");
            return Result.fail("用户名或密码错误");
        }
        
        // 登录成功，颁发 token
        StpUtil.login(loginUser.getId());
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", StpUtil.getTokenValue());
        System.out.println("Login Success! Token: " + StpUtil.getTokenValue());
        return Result.ok(tokenMap, "登录成功");
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/getUserInfo")
    public Result<?> getUserInfo() {
        try {
            // 验证 token 是否有效，无效则抛出 NotLoginException
            Object loginId = StpUtil.getLoginId();
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", loginId);
            userInfo.put("username", "admin");
            userInfo.put("realName", "系统管理员");
            userInfo.put("avatar", "https://q1.qlogo.cn/g?b=qq&nk=190848757&s=640");
            userInfo.put("desc", "管理大师");
            userInfo.put("roles", new String[]{"super"});
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
        return Result.ok(new String[]{"1000", "2000", "3000"});
    }

    /**
     * 获取动态菜单
     */
    @GetMapping("/getMenuList")
    public Result<?> getMenuList() {
        // TODO: 从 sys_menu 表读取并构建树结构，此处为硬编码演示打通前端流程
        List<Map<String, Object>> menus = new ArrayList<>();
        
        Map<String, Object> crmMenu = new HashMap<>();
        crmMenu.put("path", "/crm");
        crmMenu.put("name", "Crm");
        crmMenu.put("component", "LAYOUT");
        Map<String, Object> crmMeta = new HashMap<>();
        crmMeta.put("title", "CRM系统");
        crmMeta.put("icon", "ion:briefcase-outline");
        crmMenu.put("meta", crmMeta);
        
        List<Map<String, Object>> children = new ArrayList<>();
        
        String[][] subMenus = {
            {"customer", "客户管理"},
            {"leads", "线索池"},
            {"opportunity", "商机"},
            {"contract", "合同管理"},
            {"receivable", "回款管理"},
            {"contact", "联系人"},
            {"follow", "跟进记录"}
        };
        
        for (String[] sub : subMenus) {
            Map<String, Object> child = new HashMap<>();
            child.put("path", sub[0]);
            child.put("name", "Crm" + sub[0].substring(0, 1).toUpperCase() + sub[0].substring(1));
            child.put("component", "/crm/" + sub[0] + "/index");
            Map<String, Object> childMeta = new HashMap<>();
            childMeta.put("title", sub[1]);
            child.put("meta", childMeta);
            children.add(child);
        }
        
        crmMenu.put("children", children);
        menus.add(crmMenu);
        
        return Result.ok(menus);
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
