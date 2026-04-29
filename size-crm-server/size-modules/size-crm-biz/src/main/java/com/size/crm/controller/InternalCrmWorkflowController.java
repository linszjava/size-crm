package com.size.crm.controller;

import com.size.common.core.domain.Result;
import com.size.common.workflow.OrphanContractStubRequest;
import com.size.common.workflow.WorkflowContractSnapshot;
import com.size.common.workflow.WorkflowReceivableSnapshot;
import com.size.crm.domain.CrmContract;
import com.size.crm.domain.CrmCustomer;
import com.size.crm.domain.CrmReceivable;
import com.size.crm.service.ICrmContractService;
import com.size.crm.service.ICrmCustomerService;
import com.size.crm.service.ICrmReceivableService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 供 size-crm-workflow 调用的内部接口（不声明 Sa-Token 权限，仅限服务间调用）。
 */
@RestController
@RequestMapping("/internal/crm/workflow")
public class InternalCrmWorkflowController {

    @Autowired
    private ICrmContractService contractService;
    @Autowired
    private ICrmReceivableService receivableService;
    @Autowired
    private ICrmCustomerService customerService;

    @GetMapping("/contract/{id}/snapshot")
    public Result<WorkflowContractSnapshot> contractSnapshot(@PathVariable Long id) {
        CrmContract c = contractService.getById(id);
        return Result.ok(toContractSnapshot(c));
    }

    @GetMapping("/receivable/{id}/snapshot")
    public Result<WorkflowReceivableSnapshot> receivableSnapshot(@PathVariable Long id) {
        CrmReceivable r = receivableService.getById(id);
        return Result.ok(toReceivableSnapshot(r));
    }

    @GetMapping("/customer/{id}/name")
    public Result<String> customerName(@PathVariable Long id) {
        CrmCustomer customer = customerService.getById(id);
        if (customer == null || customer.getName() == null) {
            return Result.ok(null);
        }
        return Result.ok(customer.getName());
    }

    @PatchMapping("/contract/{id}/audit-status")
    public Result<Boolean> updateContractAuditStatus(@PathVariable Long id,
                                                       @RequestParam String status) {
        CrmContract c = contractService.getById(id);
        if (c == null) {
            return Result.ok(false);
        }
        c.setAuditStatus(status);
        c.setUpdateTime(LocalDateTime.now());
        c.setUpdateBy("workflow");
        return Result.ok(contractService.updateById(c));
    }

    @PatchMapping("/receivable/{id}/audit-status")
    public Result<Boolean> updateReceivableAuditStatus(@PathVariable Long id,
                                                       @RequestParam String status) {
        CrmReceivable r = receivableService.getById(id);
        if (r == null) {
            return Result.ok(false);
        }
        r.setAuditStatus(status);
        r.setUpdateTime(LocalDateTime.now());
        r.setUpdateBy("workflow");
        return Result.ok(receivableService.updateById(r));
    }

    @PostMapping("/contract/orphan-stub")
    public Result<Boolean> orphanContractStub(@RequestBody OrphanContractStubRequest body) {
        if (body == null || body.getId() == null) {
            return Result.fail("参数缺失");
        }
        if (contractService.getById(body.getId()) != null) {
            return Result.ok(true);
        }
        Long customerId = customerService.lambdaQuery()
                .orderByAsc(CrmCustomer::getCreateTime)
                .last("LIMIT 1")
                .oneOpt()
                .map(CrmCustomer::getId)
                .orElse(1L);
        LocalDate today = LocalDate.now();
        CrmContract stub = CrmContract.builder()
                .id(body.getId())
                .tenantId(1L)
                .customerId(customerId)
                .opportunityId(null)
                .name("历史流程补录合同-" + body.getId())
                .contractNo("AUTO-" + body.getId())
                .totalAmount(BigDecimal.ZERO)
                .signDate(today)
                .startDate(today)
                .endDate(today.plusYears(1))
                .ownerUserId(1L)
                .processInstanceId(null)
                .auditStatus("AUDITING")
                .remark("由流程业务键自动补录: " + body.getBusinessKey())
                .build();
        stub.setCreateBy("system");
        stub.setCreateTime(LocalDateTime.now());
        stub.setUpdateBy("system");
        stub.setUpdateTime(LocalDateTime.now());
        try {
            return Result.ok(contractService.save(stub));
        } catch (DuplicateKeyException e) {
            return Result.ok(contractService.getById(body.getId()) != null);
        }
    }

    private static WorkflowContractSnapshot toContractSnapshot(CrmContract c) {
        if (c == null) {
            return null;
        }
        WorkflowContractSnapshot s = new WorkflowContractSnapshot();
        s.setContractNo(c.getContractNo());
        s.setName(c.getName());
        s.setTotalAmount(c.getTotalAmount());
        s.setOwnerUserId(c.getOwnerUserId());
        s.setCustomerId(c.getCustomerId());
        s.setOpportunityId(c.getOpportunityId());
        s.setSignDate(c.getSignDate());
        s.setStartDate(c.getStartDate());
        s.setEndDate(c.getEndDate());
        s.setRemark(c.getRemark());
        return s;
    }

    private static WorkflowReceivableSnapshot toReceivableSnapshot(CrmReceivable r) {
        if (r == null) {
            return null;
        }
        WorkflowReceivableSnapshot s = new WorkflowReceivableSnapshot();
        s.setReceivableNo(r.getReceivableNo());
        s.setAmount(r.getAmount());
        s.setOwnerUserId(r.getOwnerUserId());
        s.setCustomerId(r.getCustomerId());
        s.setContractId(r.getContractId());
        s.setReturnDate(r.getReturnDate());
        s.setPayType(r.getPayType());
        s.setRemark(r.getRemark());
        return s;
    }
}
