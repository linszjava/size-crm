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
 * crm_contract 实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("crm_contract")
public class CrmContract extends BaseEntity {

    /**
     * 合同ID
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
     * 商机ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long opportunityId;

    /**
     * 合同名称
     */
    private String name;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 合同总金额
     */
    private BigDecimal totalAmount;

    /**
     * 签约日期
     */
    private LocalDate signDate;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 负责人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ownerUserId;

    /**
     * 工作流实例ID
     */
    private String processInstanceId;

    /**
     * 审核状态
     */
    private String auditStatus;

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
