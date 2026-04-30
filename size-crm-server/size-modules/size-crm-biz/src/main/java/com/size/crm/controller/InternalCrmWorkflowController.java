package com.size.crm.controller;

import com.size.common.core.domain.Result;
import com.size.common.workflow.OrphanContractStubRequest;
import com.size.common.workflow.WorkflowContractSnapshot;
import com.size.common.workflow.WorkflowReceivableSnapshot;
import com.size.crm.service.IInternalCrmWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 供 size-crm-workflow 调用的内部接口（不声明 Sa-Token 权限，仅限服务间调用）。
 */
@RestController
@RequestMapping("/internal/crm/workflow")
public class InternalCrmWorkflowController {

    @Autowired
    private IInternalCrmWorkflowService internalCrmWorkflowService;

    @GetMapping("/contract/{id}/snapshot")
    public Result<WorkflowContractSnapshot> contractSnapshot(@PathVariable Long id) {
        return Result.ok(internalCrmWorkflowService.contractSnapshot(id));
    }

    @GetMapping("/receivable/{id}/snapshot")
    public Result<WorkflowReceivableSnapshot> receivableSnapshot(@PathVariable Long id) {
        return Result.ok(internalCrmWorkflowService.receivableSnapshot(id));
    }

    @GetMapping("/customer/{id}/name")
    public Result<String> customerName(@PathVariable Long id) {
        return Result.ok(internalCrmWorkflowService.customerName(id));
    }

    @PatchMapping("/contract/{id}/audit-status")
    public Result<Boolean> updateContractAuditStatus(@PathVariable Long id,
                                                       @RequestParam String status) {
        return Result.ok(internalCrmWorkflowService.updateContractAuditStatus(id, status));
    }

    @PatchMapping("/receivable/{id}/audit-status")
    public Result<Boolean> updateReceivableAuditStatus(@PathVariable Long id,
                                                       @RequestParam String status) {
        return Result.ok(internalCrmWorkflowService.updateReceivableAuditStatus(id, status));
    }

    @PostMapping("/contract/orphan-stub")
    public Result<Boolean> orphanContractStub(@RequestBody OrphanContractStubRequest body) {
        try {
            return Result.ok(internalCrmWorkflowService.orphanContractStub(body));
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }
}
