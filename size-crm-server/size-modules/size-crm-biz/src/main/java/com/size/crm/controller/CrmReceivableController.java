package com.size.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.size.common.core.domain.Result;
import com.size.crm.domain.CrmReceivable;
import com.size.crm.service.ICrmReceivableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 回款管理 Controller
 */
@RestController
@RequestMapping("/crm/receivable")
public class CrmReceivableController {

    @Autowired
    private ICrmReceivableService receivableService;

    /**
     * 分页查询回款列表
     */
    @GetMapping("/page")
    public Result<Page<CrmReceivable>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String receivableNo,
            @RequestParam(required = false) String auditStatus,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String contractId) {

        Page<CrmReceivable> pageParam = new Page<>(current, size);
        LambdaQueryWrapper<CrmReceivable> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(receivableNo != null && !receivableNo.isEmpty(), CrmReceivable::getReceivableNo, receivableNo)
               .eq(auditStatus != null && !auditStatus.isEmpty(), CrmReceivable::getAuditStatus, auditStatus)
               .eq(customerId != null && !customerId.isEmpty(),
                   CrmReceivable::getCustomerId, customerId != null && !customerId.isEmpty() ? Long.parseLong(customerId) : 0L)
               .eq(contractId != null && !contractId.isEmpty(),
                   CrmReceivable::getContractId, contractId != null && !contractId.isEmpty() ? Long.parseLong(contractId) : 0L)
               .orderByDesc(CrmReceivable::getCreateTime);

        return Result.ok(receivableService.page(pageParam, wrapper));
    }

    /**
     * 获取回款详情
     */
    @GetMapping("/{id}")
    public Result<CrmReceivable> getInfo(@PathVariable Long id) {
        return Result.ok(receivableService.getById(id));
    }

    /**
     * 新增回款
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody CrmReceivable receivable) {
        if (receivable.getTenantId() == null) {
            receivable.setTenantId(1L);
        }
        if (receivable.getAuditStatus() == null || receivable.getAuditStatus().isEmpty()) {
            receivable.setAuditStatus("DRAFT");
        }
        return Result.ok(receivableService.save(receivable));
    }

    /**
     * 修改回款
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody CrmReceivable receivable) {
        return Result.ok(receivableService.updateById(receivable));
    }

    /**
     * 提交回款审批 (预留对接工作流)
     */
    @PostMapping("/submitAudit/{id}")
    public Result<Boolean> submitAudit(@PathVariable Long id) {
        CrmReceivable receivable = receivableService.getById(id);
        if (receivable != null && ("DRAFT".equals(receivable.getAuditStatus()) || "REJECTED".equals(receivable.getAuditStatus()))) {
            receivable.setAuditStatus("AUDITING");
            // TODO: 调用 size-crm-workflow 模块启动流程，并获取 processInstanceId 填充到这里
            return Result.ok(receivableService.updateById(receivable));
        }
        return Result.fail("只有草稿或已驳回状态的回款才能提交审批");
    }

    /**
     * 删除回款
     */
    @DeleteMapping("/{ids}")
    public Result<Boolean> remove(@PathVariable Long[] ids) {
        return Result.ok(receivableService.removeByIds(Arrays.asList(ids)));
    }
}
