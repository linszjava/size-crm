package com.size.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.size.api.domain.LoginUser;
import com.size.common.core.domain.Result;
import com.size.system.domain.SysUser;
import com.size.system.service.ISysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

/**
 * 微服务内部调用：系统登录提供者
 */
@RestController
@RequestMapping("/system/user")
public class SysLoginController {

    @Autowired
    private ISysUserService sysUserService;

    @GetMapping("/info/{username}")
    public Result<LoginUser> getUserInfo(@PathVariable("username") String username) {
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", username));
        if (sysUser == null) {
            return Result.fail("用户不存在");
        }
        
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(sysUser, loginUser);
        
        // 模拟权限和角色赋值 (真实业务需查 sys_role 和 sys_menu)
        Set<String> roles = new HashSet<>();
        roles.add("admin");
        loginUser.setRoles(roles);
        
        Set<String> perms = new HashSet<>();
        perms.add("*:*:*");
        loginUser.setPermissions(perms);
        
        return Result.ok(loginUser);
    }
}
