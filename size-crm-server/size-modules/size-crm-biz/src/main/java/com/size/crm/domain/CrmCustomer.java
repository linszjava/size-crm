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
 * crm_customer 实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("crm_customer")
public class CrmCustomer extends BaseEntity {

    /**
     * 客户ID
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
     * 客户名称
     */
    private String name;

    /**
     * 客户级别
     */
    private String level;

    /**
     * 客户来源
     */
    private String source;

    /**
     * 所属行业
     */
    private String industry;

    /**
     * 人员规模
     */
    private String scale;

    /**
     * 电话
     */
    private String phone;

    /**
     * 网址
     */
    private String website;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 成交状态
     */
    private Integer dealStatus;

    /**
     * 负责人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ownerUserId;

    /**
     * 分配时间或捞取时间
     */
    private LocalDateTime rosterTime;

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
