package com.size.system.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户分页列表视图（不携带密码）
 */
@Data
public class UserPageVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;

    private String username;

    private String nickname;

    private String email;

    private String phonenumber;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 角色名称列表（用于列表展示）
     */
    private List<String> roleNames = new ArrayList<>();
}
