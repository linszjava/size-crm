package com.size.system.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * 用户保存请求对象
 */
@Data
public class UserSaveDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;

    private String username;

    private String nickname;

    private String password;

    private String phonenumber;

    private String email;

    private Integer status;

    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<Long> roleIds;
}
