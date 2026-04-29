package com.size.system.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色详情视图对象
 */
@Data
public class RoleDetailVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    private String roleName;

    private String roleKey;

    private Integer roleSort;

    private String dataScope;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<Long> menuIds;
}
