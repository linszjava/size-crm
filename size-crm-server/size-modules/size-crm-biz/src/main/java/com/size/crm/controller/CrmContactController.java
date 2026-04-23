package com.size.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.size.common.core.domain.Result;
import com.size.crm.domain.CrmContact;
import com.size.crm.service.ICrmContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 联系人管理 Controller
 */
@RestController
@RequestMapping("/crm/contact")
public class CrmContactController {

    @Autowired
    private ICrmContactService contactService;

    /**
     * 分页查询联系人列表
     */
    @GetMapping("/page")
    public Result<Page<CrmContact>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String customerId) {

        Page<CrmContact> pageParam = new Page<>(current, size);
        LambdaQueryWrapper<CrmContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), CrmContact::getName, name)
               .like(phone != null && !phone.isEmpty(), CrmContact::getPhone, phone)
               .eq(customerId != null && !customerId.isEmpty(),
                   CrmContact::getCustomerId, customerId != null && !customerId.isEmpty() ? Long.parseLong(customerId) : 0L)
               .orderByDesc(CrmContact::getCreateTime);

        return Result.ok(contactService.page(pageParam, wrapper));
    }

    /**
     * 获取联系人详情
     */
    @GetMapping("/{id}")
    public Result<CrmContact> getInfo(@PathVariable Long id) {
        return Result.ok(contactService.getById(id));
    }

    /**
     * 新增联系人
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody CrmContact contact) {
        if (contact.getTenantId() == null) {
            contact.setTenantId(1L);
        }
        return Result.ok(contactService.save(contact));
    }

    /**
     * 修改联系人
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody CrmContact contact) {
        return Result.ok(contactService.updateById(contact));
    }

    /**
     * 删除联系人
     */
    @DeleteMapping("/{ids}")
    public Result<Boolean> remove(@PathVariable Long[] ids) {
        return Result.ok(contactService.removeByIds(Arrays.asList(ids)));
    }
}
