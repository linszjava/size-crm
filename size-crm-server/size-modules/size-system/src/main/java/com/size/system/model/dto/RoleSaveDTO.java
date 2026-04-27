package com.size.system.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * 角色保存请求对象
 */
@Data
public class RoleSaveDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    private String roleName;

    private String roleKey;

    private Integer roleSort;

    private String dataScope;

    private Integer status;

    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<Long> menuIds;
}
