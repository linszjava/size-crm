package com.size.api.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求对象
 */
@Data
public class UserRegisterDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;

    private String mobile;

    private String password;
}
