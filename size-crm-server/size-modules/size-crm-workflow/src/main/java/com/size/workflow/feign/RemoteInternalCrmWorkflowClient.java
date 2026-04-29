package com.size.workflow.feign;

import com.size.common.core.domain.Result;
import com.size.common.workflow.OrphanContractStubRequest;
import com.size.common.workflow.WorkflowContractSnapshot;
import com.size.common.workflow.WorkflowReceivableSnapshot;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteInternalCrmWorkflow", value = "size-crm-biz")
public interface RemoteInternalCrmWorkflowClient {

    @GetMapping("/internal/crm/workflow/contract/{id}/snapshot")
    Result<WorkflowContractSnapshot> getContractSnapshot(@PathVariable("id") Long id);

    @GetMapping("/internal/crm/workflow/receivable/{id}/snapshot")
    Result<WorkflowReceivableSnapshot> getReceivableSnapshot(@PathVariable("id") Long id);

    @GetMapping("/internal/crm/workflow/customer/{id}/name")
    Result<String> getCustomerName(@PathVariable("id") Long id);

    @PostMapping("/internal/crm/workflow/contract/orphan-stub")
    Result<Boolean> orphanContractStub(@RequestBody OrphanContractStubRequest body);

    @PatchMapping("/internal/crm/workflow/contract/{id}/audit-status")
    Result<Boolean> updateContractAuditStatus(@PathVariable("id") Long id,
                                             @RequestParam("status") String status);

    @PatchMapping("/internal/crm/workflow/receivable/{id}/audit-status")
    Result<Boolean> updateReceivableAuditStatus(@PathVariable("id") Long id,
                                               @RequestParam("status") String status);
}
