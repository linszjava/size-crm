package com.size.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.size.common.core.domain.Result;
import com.size.crm.domain.CrmCustomer;
import com.size.crm.domain.CrmLeads;
import com.size.crm.service.ICrmCustomerService;
import com.size.crm.service.ICrmLeadsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 线索池 Controller
 */
@RestController
@RequestMapping("/crm/leads")
public class CrmLeadsController {

    @Autowired
    private ICrmLeadsService leadsService;

    @Autowired
    private ICrmCustomerService customerService;

    /**
     * 分页查询线索列表
     */
    @GetMapping("/page")
    public Result<Page<CrmLeads>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String intention) {

        Page<CrmLeads> pageParam = new Page<>(current, size);
        LambdaQueryWrapper<CrmLeads> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), CrmLeads::getName, name)
               .like(phone != null && !phone.isEmpty(), CrmLeads::getPhone, phone)
               .eq(status != null && !status.isEmpty(), CrmLeads::getStatus, status)
               .eq(source != null && !source.isEmpty(), CrmLeads::getSource, source)
               .eq(intention != null && !intention.isEmpty(), CrmLeads::getIntention, intention)
               .orderByDesc(CrmLeads::getCreateTime);

        return Result.ok(leadsService.page(pageParam, wrapper));
    }

    /**
     * 获取线索详情
     */
    @GetMapping("/{id}")
    public Result<CrmLeads> getInfo(@PathVariable Long id) {
        return Result.ok(leadsService.getById(id));
    }

    /**
     * 新增线索
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody CrmLeads leads) {
        if (leads.getTenantId() == null) {
            leads.setTenantId(1L);
        }
        if (leads.getStatus() == null) {
            leads.setStatus("UNASSIGNED");
        }
        return Result.ok(leadsService.save(leads));
    }

    /**
     * 修改线索
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody CrmLeads leads) {
        return Result.ok(leadsService.updateById(leads));
    }

    /**
     * 删除线索
     */
    @DeleteMapping("/{ids}")
    public Result<Boolean> remove(@PathVariable Long[] ids) {
        return Result.ok(leadsService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 一键转化线索为客户
     * 将线索信息复制到客户表，并标记线索状态为已转化
     */
    @PostMapping("/convert/{id}")
    public Result<Long> convert(@PathVariable Long id) {
        CrmLeads leads = leadsService.getById(id);
        if (leads == null) {
            return Result.fail("线索不存在");
        }
        if ("CONVERTED".equals(leads.getStatus())) {
            return Result.fail("该线索已转化为客户");
        }

        // 构建客户对象，从线索信息映射
        CrmCustomer customer = new CrmCustomer();
        customer.setTenantId(leads.getTenantId());
        customer.setName(leads.getCompanyName() != null ? leads.getCompanyName() : leads.getName());
        customer.setPhone(leads.getPhone());
        customer.setIndustry(leads.getIndustry());
        customer.setSource(leads.getSource());
        customer.setOwnerUserId(leads.getOwnerUserId());
        customer.setRemark("由线索转化，联系人：" + leads.getName()
                + (leads.getPosition() != null ? "（" + leads.getPosition() + "）" : ""));
        customer.setDealStatus(0);
        customer.setRosterTime(LocalDateTime.now());
        customerService.save(customer);

        // 更新线索状态为已转化
        leadsService.update(new LambdaUpdateWrapper<CrmLeads>()
                .eq(CrmLeads::getId, id)
                .set(CrmLeads::getStatus, "CONVERTED")
                .set(CrmLeads::getCustomerId, customer.getId())
                .set(CrmLeads::getConvertTime, LocalDateTime.now()));

        return Result.ok(customer.getId(), "转化成功");
    }
}
