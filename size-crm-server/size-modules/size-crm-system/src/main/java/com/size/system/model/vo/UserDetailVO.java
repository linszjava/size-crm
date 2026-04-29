package com.size.system.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户详情视图对象
 */
@Data
public class UserDetailVO {
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

    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<Long> roleIds;
}
