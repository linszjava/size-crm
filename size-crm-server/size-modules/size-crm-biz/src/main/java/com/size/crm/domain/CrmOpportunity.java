package com.size.crm.domain;

import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.size.common.core.domain.BaseEntity;

/**
 * crm_opportunity 实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("crm_opportunity")
public class CrmOpportunity extends BaseEntity {

    /**
     * 商机ID
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
     * 客户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerId;

    /**
     * 商机名称
     */
    private String name;

    /**
     * 预计销售金额
     */
    private BigDecimal expectedAmount;

    /**
     * 预计签单日期
     */
    private LocalDate expectedDate;

    /**
     * 销售阶段
     */
    private String salesStage;

    /**
     * 赢单率(%)
     */
    private BigDecimal winRate;

    /**
     * 负责人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ownerUserId;

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
