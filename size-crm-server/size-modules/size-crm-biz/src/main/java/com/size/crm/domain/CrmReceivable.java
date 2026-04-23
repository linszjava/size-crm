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
 * crm_receivable 实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("crm_receivable")
public class CrmReceivable extends BaseEntity {

    /**
     * 回款ID
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
     * 合同ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractId;

    /**
     * 回款编号
     */
    private String receivableNo;

    /**
     * 回款金额
     */
    private BigDecimal amount;

    /**
     * 回款日期
     */
    private LocalDate returnDate;

    /**
     * 支付方式
     */
    private String payType;

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
