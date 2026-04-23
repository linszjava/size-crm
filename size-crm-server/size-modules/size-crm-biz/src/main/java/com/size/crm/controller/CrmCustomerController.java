package com.size.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.size.common.core.domain.Result;
import com.size.crm.domain.CrmCustomer;
import com.size.crm.service.ICrmCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 客户管理 Controller
 */
@RestController
@RequestMapping("/crm/customer")
public class CrmCustomerController {

    @Autowired
    private ICrmCustomerService crmCustomerService;

    /**
     * 分页查询客户列表
     */
    @GetMapping("/page")
    public Result<Page<CrmCustomer>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            CrmCustomer crmCustomer) {
        
        Page<CrmCustomer> page = new Page<>(current, size);
        QueryWrapper<CrmCustomer> queryWrapper = new QueryWrapper<>();
        
        if (crmCustomer.getName() != null && !crmCustomer.getName().isEmpty()) {
            queryWrapper.like("name", crmCustomer.getName());
        }
        if (crmCustomer.getPhone() != null && !crmCustomer.getPhone().isEmpty()) {
            queryWrapper.like("phone", crmCustomer.getPhone());
        }
        if (crmCustomer.getLevel() != null && !crmCustomer.getLevel().isEmpty()) {
            queryWrapper.eq("level", crmCustomer.getLevel());
        }
        
        // 按照创建时间降序排序
        queryWrapper.orderByDesc("create_time");
        
        return Result.ok(crmCustomerService.page(page, queryWrapper));
    }

    /**
     * 获取客户详情
     */
    @GetMapping("/{id}")
    public Result<CrmCustomer> getInfo(@PathVariable("id") Long id) {
        return Result.ok(crmCustomerService.getById(id));
    }

    /**
     * 新增客户
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody CrmCustomer crmCustomer) {
        // 设置默认多租户 (方便联调，实际开发可从Token获取)
        if (crmCustomer.getTenantId() == null) {
            crmCustomer.setTenantId(1L);
        }
        return Result.ok(crmCustomerService.save(crmCustomer));
    }

    /**
     * 修改客户
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody CrmCustomer crmCustomer) {
        return Result.ok(crmCustomerService.updateById(crmCustomer));
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{ids}")
    public Result<Boolean> remove(@PathVariable Long[] ids) {
        return Result.ok(crmCustomerService.removeByIds(Arrays.asList(ids)));
    }
}
