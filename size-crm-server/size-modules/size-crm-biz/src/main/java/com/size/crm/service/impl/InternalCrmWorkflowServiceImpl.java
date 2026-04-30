package com.size.crm.service.impl;

import com.size.common.workflow.OrphanContractStubRequest;
import com.size.common.workflow.WorkflowContractSnapshot;
import com.size.common.workflow.WorkflowReceivableSnapshot;
import com.size.crm.domain.CrmContract;
import com.size.crm.domain.CrmCustomer;
import com.size.crm.domain.CrmReceivable;
import com.size.crm.service.ICrmContractService;
import com.size.crm.service.ICrmCustomerService;
import com.size.crm.service.ICrmReceivableService;
import com.size.crm.service.IInternalCrmWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class InternalCrmWorkflowServiceImpl implements IInternalCrmWorkflowService {

    @Autowired
    private ICrmContractService contractService;
    @Autowired
    private ICrmReceivableService receivableService;
    @Autowired
    private ICrmCustomerService customerService;

    @Override
    public WorkflowContractSnapshot contractSnapshot(Long id) {
        CrmContract c = contractService.getById(id);
        return toContractSnapshot(c);
    }

    @Override
    public WorkflowReceivableSnapshot receivableSnapshot(Long id) {
        CrmReceivable r = receivableService.getById(id);
        return toReceivableSnapshot(r);
    }

    @Override
    public String customerName(Long id) {
        CrmCustomer customer = customerService.getById(id);
        if (customer == null || customer.getName() == null) {
            return null;
        }
        return customer.getName();
    }

    @Override
    public boolean updateContractAuditStatus(Long id, String status) {
        CrmContract c = contractService.getById(id);
        if (c == null) {
            return false;
        }
        c.setAuditStatus(status);
        c.setUpdateTime(LocalDateTime.now());
        c.setUpdateBy("workflow");
        return contractService.updateById(c);
    }

    @Override
    public boolean updateReceivableAuditStatus(Long id, String status) {
        CrmReceivable r = receivableService.getById(id);
        if (r == null) {
            return false;
        }
        r.setAuditStatus(status);
        r.setUpdateTime(LocalDateTime.now());
        r.setUpdateBy("workflow");
        return receivableService.updateById(r);
    }

    @Override
    public boolean orphanContractStub(OrphanContractStubRequest body) {
        if (body == null || body.getId() == null) {
            throw new IllegalArgumentException("参数缺失");
        }
        if (contractService.getById(body.getId()) != null) {
            return true;
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
            return contractService.save(stub);
        } catch (DuplicateKeyException e) {
            return contractService.getById(body.getId()) != null;
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
