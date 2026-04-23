package com.size.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.size.common.core.domain.Result;
import com.size.crm.domain.CrmOpportunity;
import com.size.crm.service.ICrmOpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 商机管理 Controller
 */
@RestController
@RequestMapping("/crm/opportunity")
public class CrmOpportunityController {

    @Autowired
    private ICrmOpportunityService opportunityService;

    /**
     * 分页查询商机列表
     */
    @GetMapping("/page")
    public Result<Page<CrmOpportunity>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String salesStage,
            @RequestParam(required = false) String customerId) {

        Page<CrmOpportunity> pageParam = new Page<>(current, size);
        LambdaQueryWrapper<CrmOpportunity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), CrmOpportunity::getName, name)
               .eq(salesStage != null && !salesStage.isEmpty(), CrmOpportunity::getSalesStage, salesStage)
               .eq(customerId != null && !customerId.isEmpty(),
                   CrmOpportunity::getCustomerId, customerId != null && !customerId.isEmpty() ? Long.parseLong(customerId) : 0L)
               .orderByDesc(CrmOpportunity::getCreateTime);

        return Result.ok(opportunityService.page(pageParam, wrapper));
    }

    /**
     * 获取商机详情
     */
    @GetMapping("/{id}")
    public Result<CrmOpportunity> getInfo(@PathVariable Long id) {
        return Result.ok(opportunityService.getById(id));
    }

    /**
     * 新增商机
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody CrmOpportunity opportunity) {
        if (opportunity.getTenantId() == null) {
            opportunity.setTenantId(1L);
        }
        return Result.ok(opportunityService.save(opportunity));
    }

    /**
     * 修改商机
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody CrmOpportunity opportunity) {
        return Result.ok(opportunityService.updateById(opportunity));
    }

    /**
     * 删除商机
     */
    @DeleteMapping("/{ids}")
    public Result<Boolean> remove(@PathVariable Long[] ids) {
        return Result.ok(opportunityService.removeByIds(Arrays.asList(ids)));
    }
}
