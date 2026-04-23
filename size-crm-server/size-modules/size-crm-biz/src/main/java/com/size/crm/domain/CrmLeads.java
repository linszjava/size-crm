package com.size.crm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.size.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * crm_leads 线索实体类
 * status: UNASSIGNED-待分配 FOLLOWING-跟进中 CONVERTED-已转化 INVALID-已无效
 * source: REFERRAL-转介绍 WEBSITE-官网 EXHIBITION-展会 CALL-电话外呼 AD-广告投放 OTHER-其他
 * intention: HIGH-高意向 MEDIUM-中意向 LOW-低意向
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("crm_leads")
public class CrmLeads extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    /** 线索姓名 */
    private String name;

    /** 手机号 */
    private String phone;

    /** 微信号 */
    private String wechat;

    /** 邮箱 */
    private String email;

    /** 所在公司 */
    private String companyName;

    /** 职位 */
    private String position;

    /** 所属行业 */
    private String industry;

    /** 线索来源 */
    private String source;

    /** 线索状态 */
    private String status;

    /** 意向度 */
    private String intention;

    /** 负责人用户ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ownerUserId;

    /** 最近跟进时间 */
    private LocalDateTime lastFollowTime;

    /** 下次跟进时间 */
    private LocalDateTime nextFollowTime;

    /** 转化后的客户ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerId;

    /** 转化时间 */
    private LocalDateTime convertTime;

    /** 备注 */
    private String remark;

    @TableLogic
    private Integer delFlag;
}
