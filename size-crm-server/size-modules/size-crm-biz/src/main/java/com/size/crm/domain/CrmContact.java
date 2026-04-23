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
 * crm_contact 实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("crm_contact")
public class CrmContact extends BaseEntity {

    /**
     * 联系人ID
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
     * 所属客户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 职务
     */
    private String post;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 座机
     */
    private String telephone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 微信号
     */
    private String wechat;

    /**
     * 地址
     */
    private String address;

    /**
     * 是否关键决策人
     */
    private Integer isPrimary;

    /**
     * 下次跟进时间
     */
    private LocalDateTime nextFollowTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志
     */
    @TableLogic
    private Integer delFlag;
}
