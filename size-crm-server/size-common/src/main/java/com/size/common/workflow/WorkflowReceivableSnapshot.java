package com.size.common.workflow;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 工作流待办/进度中回款业务摘要（由 CRM 服务填充）
 */
@Data
public class WorkflowReceivableSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    private String receivableNo;
    private BigDecimal amount;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ownerUserId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractId;
    private LocalDate returnDate;
    private String payType;
    private String remark;
}
