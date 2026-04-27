package com.size.api.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 登录用户权限聚合信息
 */
@Data
@NoArgsConstructor
public class UserPermissionInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String username;

    private String realName;

    private String avatar;

    /**
     * 角色编码集合（role_key）
     */
    private Set<String> roles = new LinkedHashSet<>();

    /**
     * 权限点集合（如 system:menu:add）
     */
    private Set<String> permCodes = new LinkedHashSet<>();

    /**
     * 前端动态路由树（目录/菜单）
     */
    private List<Map<String, Object>> menuRouteTree = new ArrayList<>();
}
