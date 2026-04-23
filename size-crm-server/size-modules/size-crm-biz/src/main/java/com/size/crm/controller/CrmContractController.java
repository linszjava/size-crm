package com.size.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.size.common.core.domain.Result;
import com.size.crm.domain.CrmContract;
import com.size.crm.service.ICrmContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import com.size.crm.feign.RemoteWorkflowService;

/**
 * 合同管理 Controller
 */
@RestController
@RequestMapping("/crm/contract")
public class CrmContractController {

    @Autowired
    private ICrmContractService contractService;

    @Autowired
    private RemoteWorkflowService remoteWorkflowService;

    /**
     * 分页查询合同列表
     */
    @GetMapping("/page")
    public Result<Page<CrmContract>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String contractNo,
            @RequestParam(required = false) String auditStatus,
            @RequestParam(required = false) String customerId) {

        Page<CrmContract> pageParam = new Page<>(current, size);
        LambdaQueryWrapper<CrmContract> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), CrmContract::getName, name)
               .like(contractNo != null && !contractNo.isEmpty(), CrmContract::getContractNo, contractNo)
               .eq(auditStatus != null && !auditStatus.isEmpty(), CrmContract::getAuditStatus, auditStatus)
               .eq(customerId != null && !customerId.isEmpty(),
                   CrmContract::getCustomerId, customerId != null && !customerId.isEmpty() ? Long.parseLong(customerId) : 0L)
               .orderByDesc(CrmContract::getCreateTime);

        return Result.ok(contractService.page(pageParam, wrapper));
    }

    /**
     * 获取合同详情
     */
    @GetMapping("/{id}")
    public Result<CrmContract> getInfo(@PathVariable Long id) {
        return Result.ok(contractService.getById(id));
    }

    /**
     * 新增合同
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody CrmContract contract) {
        if (contract.getTenantId() == null) {
            contract.setTenantId(1L);
        }
        if (contract.getAuditStatus() == null || contract.getAuditStatus().isEmpty()) {
            contract.setAuditStatus("DRAFT"); // 默认草稿状态
        }
        return Result.ok(contractService.save(contract));
    }

    /**
     * 修改合同
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody CrmContract contract) {
        return Result.ok(contractService.updateById(contract));
    }

    /**
     * 提交合同审批 (预留对接工作流)
     */
    @PostMapping("/submitAudit/{id}")
    public Result<Boolean> submitAudit(@PathVariable Long id) {
        CrmContract contract = contractService.getById(id);
        if (contract != null && ("DRAFT".equals(contract.getAuditStatus()) || "REJECTED".equals(contract.getAuditStatus()))) {
            contract.setAuditStatus("AUDITING");
            // 调用 size-workflow 模块启动流程
            // 假设发起人是 contract.getOwnerUserId()，如果为空则默认 10001
            String initiatorId = contract.getOwnerUserId() != null ? contract.getOwnerUserId().toString() : "10001";
            Result<String> wfResult = remoteWorkflowService.startProcess("contract-audit", "contract:" + id, initiatorId);
            
            if (wfResult.getCode() == 200) {
                contract.setProcessInstanceId(wfResult.getData());
            } else {
                return Result.fail("工作流启动失败: " + wfResult.getMsg());
            }
            return Result.ok(contractService.updateById(contract));
        }
        return Result.fail("只有草稿或已驳回状态的合同才能提交审批");
    }

    /**
     * 删除合同
     */
    @DeleteMapping("/{ids}")
    public Result<Boolean> remove(@PathVariable Long[] ids) {
        return Result.ok(contractService.removeByIds(Arrays.asList(ids)));
    }
}
