package com.size.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.size.api.domain.LoginUser;
import com.size.api.domain.UserRegisterDTO;
import com.size.api.domain.UserPermissionInfo;
import com.size.common.core.domain.Result;
import com.size.system.domain.SysMenu;
import com.size.system.domain.SysRole;
import com.size.system.domain.SysRoleMenu;
import com.size.system.domain.SysUser;
import com.size.system.domain.SysUserRole;
import com.size.system.service.ISysMenuService;
import com.size.system.service.ISysRoleMenuService;
import com.size.system.service.ISysRoleService;
import com.size.system.service.ISysUserService;
import com.size.system.service.ISysUserRoleService;
import cn.hutool.crypto.digest.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 微服务内部调用：系统登录提供者
 */
@RestController
@RequestMapping("/system/user")
public class SysLoginController {

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private ISysRoleMenuService sysRoleMenuService;

    @Autowired
    private ISysMenuService sysMenuService;

    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody UserRegisterDTO dto) {
        if (dto == null || dto.getUsername() == null || dto.getUsername().isBlank()) {
            return Result.fail("用户名不能为空");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            return Result.fail("密码不能为空");
        }
        Long exists = sysUserService.count(
                new QueryWrapper<SysUser>().lambda().eq(SysUser::getUsername, dto.getUsername())
        );
        if (exists != null && exists > 0) {
            return Result.fail("用户账号已存在，请更换");
        }

        SysUser user = new SysUser();
        user.setTenantId(1L);
        user.setUsername(dto.getUsername().trim());
        user.setNickname(dto.getUsername().trim());
        user.setPhonenumber(dto.getMobile());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setStatus(0);
        boolean saved = sysUserService.save(user);
        if (!saved) {
            return Result.fail("注册失败");
        }

        // 默认赋予普通员工角色（common）
        SysRole commonRole = sysRoleService.getOne(
                new QueryWrapper<SysRole>().lambda()
                        .eq(SysRole::getRoleKey, "common")
                        .eq(SysRole::getStatus, 0)
                        .last("limit 1"),
                false
        );
        if (commonRole != null) {
            sysUserRoleService.save(SysUserRole.builder()
                    .userId(user.getId())
                    .roleId(commonRole.getId())
                    .build());
        }
        return Result.ok(true, "注册成功");
    }

    @GetMapping("/info/{username}")
    public Result<LoginUser> getUserInfo(@PathVariable("username") String username) {
        SysUser sysUser = sysUserService.getOne(
                new QueryWrapper<SysUser>().lambda()
                        .eq(SysUser::getUsername, username)
                        .orderByAsc(SysUser::getId)
                        .last("limit 1"),
                false
        );
        if (sysUser == null) {
            return Result.fail("用户不存在");
        }
        
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(sysUser, loginUser);

        UserPermissionInfo permissionInfo = buildPermissionInfo(sysUser);
        loginUser.setRoles(permissionInfo.getRoles());
        loginUser.setPermissions(permissionInfo.getPermCodes());

        return Result.ok(loginUser);
    }

    @GetMapping("/permission/{userId}")
    public Result<UserPermissionInfo> getUserPermission(@PathVariable("userId") Long userId) {
        SysUser sysUser = sysUserService.getById(userId);
        if (sysUser == null) {
            return Result.fail("用户不存在");
        }
        return Result.ok(buildPermissionInfo(sysUser));
    }

    private UserPermissionInfo buildPermissionInfo(SysUser sysUser) {
        UserPermissionInfo permissionInfo = new UserPermissionInfo();
        permissionInfo.setUserId(sysUser.getId());
        permissionInfo.setUsername(sysUser.getUsername());
        permissionInfo.setRealName(sysUser.getNickname());
        permissionInfo.setAvatar(sysUser.getAvatar());

        List<Long> roleIds = sysUserRoleService.list(
                new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, sysUser.getId())
        ).stream().map(SysUserRole::getRoleId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        if (roleIds.isEmpty()) {
            permissionInfo.setRoles(Collections.emptySet());
            permissionInfo.setPermCodes(Collections.emptySet());
            permissionInfo.setMenuRouteTree(Collections.emptyList());
            return permissionInfo;
        }

        List<SysRole> roles = sysRoleService.list(
                new QueryWrapper<SysRole>().lambda()
                        .in(SysRole::getId, roleIds)
                        .eq(SysRole::getStatus, 0)
        );
        Set<String> roleCodes = roles.stream()
                .map(SysRole::getRoleKey)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        permissionInfo.setRoles(roleCodes);

        List<Long> enabledRoleIds = roles.stream().map(SysRole::getId).collect(Collectors.toList());
        if (enabledRoleIds.isEmpty()) {
            permissionInfo.setPermCodes(Collections.emptySet());
            permissionInfo.setMenuRouteTree(Collections.emptyList());
            return permissionInfo;
        }

        List<Long> menuIds = sysRoleMenuService.list(
                new QueryWrapper<SysRoleMenu>().lambda().in(SysRoleMenu::getRoleId, enabledRoleIds)
        ).stream().map(SysRoleMenu::getMenuId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (menuIds.isEmpty()) {
            permissionInfo.setPermCodes(Collections.emptySet());
            permissionInfo.setMenuRouteTree(Collections.emptyList());
            return permissionInfo;
        }

        List<SysMenu> menus = sysMenuService.list(
                new QueryWrapper<SysMenu>().lambda()
                        .in(SysMenu::getId, menuIds)
                        .eq(SysMenu::getStatus, 0)
        );
        menus.sort(Comparator.comparing(SysMenu::getParentId, Comparator.nullsFirst(Long::compareTo))
                .thenComparing(SysMenu::getOrderNum, Comparator.nullsFirst(Integer::compareTo)));

        Set<String> permCodes = menus.stream()
                .filter(menu -> "F".equalsIgnoreCase(menu.getMenuType()))
                .map(SysMenu::getPerms)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        permissionInfo.setPermCodes(permCodes);
        permissionInfo.setMenuRouteTree(buildRouteTree(menus));
        return permissionInfo;
    }

    private List<Map<String, Object>> buildRouteTree(List<SysMenu> menus) {
        List<SysMenu> routeMenus = menus.stream()
                .filter(menu -> "M".equalsIgnoreCase(menu.getMenuType()) || "C".equalsIgnoreCase(menu.getMenuType()))
                .filter(menu -> Integer.valueOf(1).equals(menu.getVisible()))
                .collect(Collectors.toList());

        if (routeMenus.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Map<String, Object>> nodeMap = new LinkedHashMap<>();
        for (SysMenu menu : routeMenus) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("path", menu.getPath());
            node.put("name", generateRouteName(menu));
            String component = menu.getComponent();
            if ((component == null || component.isBlank()) && Long.valueOf(0L).equals(menu.getParentId())) {
                component = "LAYOUT";
            }
            if (component != null && !component.isBlank()) {
                node.put("component", component);
            }
            Map<String, Object> meta = new LinkedHashMap<>();
            meta.put("title", menu.getMenuName());
            if (menu.getIcon() != null && !menu.getIcon().isBlank() && !"#".equals(menu.getIcon())) {
                meta.put("icon", menu.getIcon());
            }
            node.put("meta", meta);
            node.put("children", new ArrayList<Map<String, Object>>());
            nodeMap.put(menu.getId(), node);
        }

        List<Map<String, Object>> roots = new ArrayList<>();
        for (SysMenu menu : routeMenus) {
            Map<String, Object> current = nodeMap.get(menu.getId());
            Long parentId = menu.getParentId();
            if (parentId == null || parentId == 0L || !nodeMap.containsKey(parentId)) {
                roots.add(current);
                continue;
            }
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> children =
                    (List<Map<String, Object>>) nodeMap.get(parentId).get("children");
            children.add(current);
        }

        // 目录节点补 redirect，避免直接访问如 /dashboard 命中 404
        applyRedirectRecursively(roots);
        return roots;
    }

    @SuppressWarnings("unchecked")
    private void applyRedirectRecursively(List<Map<String, Object>> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        for (Map<String, Object> node : nodes) {
            List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
            if (children != null && !children.isEmpty()) {
                Object currentPathObj = node.get("path");
                Object firstChildPathObj = children.get(0).get("path");
                if (currentPathObj instanceof String currentPath && firstChildPathObj instanceof String childPath) {
                    String redirect = currentPath.endsWith("/") ? currentPath + childPath : currentPath + "/" + childPath;
                    node.put("redirect", redirect.replaceAll("//+", "/"));
                }
                applyRedirectRecursively(children);
            }
        }
    }

    private String generateRouteName(SysMenu menu) {
        if (menu.getPath() == null || menu.getPath().isBlank()) {
            return "Menu" + menu.getId();
        }
        String normalized = menu.getPath().replace("/", "_").replace("-", "_");
        String[] pieces = normalized.split("_");
        StringBuilder builder = new StringBuilder();
        for (String piece : pieces) {
            if (piece == null || piece.isBlank()) {
                continue;
            }
            String lower = piece.toLowerCase(Locale.ROOT);
            builder.append(lower.substring(0, 1).toUpperCase(Locale.ROOT)).append(lower.substring(1));
        }
        if (builder.length() == 0) {
            builder.append("Menu");
        }
        builder.append(menu.getId());
        return builder.toString();
    }
}
