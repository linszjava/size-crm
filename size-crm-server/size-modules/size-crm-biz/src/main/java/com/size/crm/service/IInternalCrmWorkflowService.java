package com.size.crm.service;

import com.size.common.workflow.OrphanContractStubRequest;
import com.size.common.workflow.WorkflowContractSnapshot;
import com.size.common.workflow.WorkflowReceivableSnapshot;

/**
 * 供工作流服务间调用的 CRM 读写（内部接口对应业务）
 */
public interface IInternalCrmWorkflowService {

    WorkflowContractSnapshot contractSnapshot(Long id);

    WorkflowReceivableSnapshot receivableSnapshot(Long id);

    String customerName(Long id);

    boolean updateContractAuditStatus(Long id, String status);

    boolean updateReceivableAuditStatus(Long id, String status);

    boolean orphanContractStub(OrphanContractStubRequest body);
}
