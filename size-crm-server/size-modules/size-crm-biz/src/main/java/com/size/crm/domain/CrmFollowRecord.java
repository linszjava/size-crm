package com.size.crm.domain;

import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.size.common.core.domain.BaseEntity;

/**
 * crm_follow_record 实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("crm_follow_record")
public class CrmFollowRecord extends BaseEntity {

    /**
     * 记录ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 租户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;

    /**
     * 跟进方式
     */
    private String followType;

    /**
     * 跟进内容
     */
    private String content;

    /**
     * 下次联系时间
     */
    private LocalDateTime nextTime;

    /**
     * 记录人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long recordUserId;

    /**
     * 删除标志
     */
    @TableLogic
    private Integer delFlag;
}
