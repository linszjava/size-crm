package com.size.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.size.common.core.domain.Result;
import com.size.crm.domain.CrmFollowRecord;
import com.size.crm.service.ICrmFollowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 跟进记录 Controller
 */
@RestController
@RequestMapping("/crm/follow")
public class CrmFollowRecordController {

    @Autowired
    private ICrmFollowRecordService followRecordService;

    /**
     * 分页查询跟进记录
     */
    @GetMapping("/page")
    public Result<Page<CrmFollowRecord>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) Long bizId,
            @RequestParam(required = false) String followType,
            @RequestParam(required = false) Long recordUserId,
            @RequestParam(required = false) String content) {

        Page<CrmFollowRecord> pageParam = new Page<>(current, size);
        LambdaQueryWrapper<CrmFollowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(bizType != null && !bizType.isEmpty(), CrmFollowRecord::getBizType, bizType)
                .eq(bizId != null, CrmFollowRecord::getBizId, bizId)
                .eq(followType != null && !followType.isEmpty(), CrmFollowRecord::getFollowType, followType)
                .eq(recordUserId != null, CrmFollowRecord::getRecordUserId, recordUserId)
                .like(content != null && !content.isEmpty(), CrmFollowRecord::getContent, content)
                .orderByDesc(CrmFollowRecord::getCreateTime);

        return Result.ok(followRecordService.page(pageParam, wrapper));
    }

    /**
     * 查询详情
     */
    @GetMapping("/{id}")
    public Result<CrmFollowRecord> getInfo(@PathVariable Long id) {
        return Result.ok(followRecordService.getById(id));
    }

    /**
     * 新增记录
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody CrmFollowRecord followRecord) {
        if (followRecord.getTenantId() == null) {
            followRecord.setTenantId(1L);
        }
        return Result.ok(followRecordService.save(followRecord));
    }

    /**
     * 修改记录
     */
    @PutMapping
    public Result<Boolean> edit(@RequestBody CrmFollowRecord followRecord) {
        return Result.ok(followRecordService.updateById(followRecord));
    }

    /**
     * 删除记录
     */
    @DeleteMapping("/{ids}")
    public Result<Boolean> remove(@PathVariable Long[] ids) {
        return Result.ok(followRecordService.removeByIds(Arrays.asList(ids)));
    }
}

