package com.size.api.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Set;

/**
 * 用户信息 DTO，用于微服务间 RPC 传输
 */
@Data
@NoArgsConstructor
public class LoginUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户昵称/真实姓名
     */
    private String realName;

    /**
     * 密码 (密文)
     */
    private String password;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 角色列表
     */
    private Set<String> roles;

    /**
     * 权限标识列表
     */
    private Set<String> permissions;
}
