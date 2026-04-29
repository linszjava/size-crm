package com.size.common.workflow;

import lombok.Data;

import java.io.Serializable;

/**
 * 历史流程仅有 businessKey 而无合同行时的占位补录请求
 */
@Data
public class OrphanContractStubRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String businessKey;
}
