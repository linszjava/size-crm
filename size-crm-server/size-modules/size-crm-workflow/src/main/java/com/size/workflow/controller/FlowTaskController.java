package com.size.workflow.controller;

import com.size.common.core.domain.Result;
import com.size.workflow.model.dto.TaskCompleteDTO;
import com.size.workflow.service.IFlowTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * 工作流任务 HTTP 入口（编排逻辑见 {@link IFlowTaskService}）
 */
@RestController
@RequestMapping("/workflow/task")
public class FlowTaskController {

    @Autowired
    private IFlowTaskService flowTaskService;

    /**
     * 发起流程
     */
    @PostMapping("/start")
    public Result<String> startProcess(@RequestParam String processDefinitionKey,
                                       @RequestParam String businessKey,
                                       @RequestParam String userId) {
        String instanceId = flowTaskService.startProcess(processDefinitionKey, businessKey, userId);
        return Result.ok(instanceId, "流程启动成功");
    }

    /**
     * 查询我的待办任务 (结合用户ID和角色)。
     * roleKeys 在 GET + axios 下常为 roleKeys[] 序列化，Spring 绑定不到 List，故增加 roleKeysCsv（逗号分隔）兜底。
     */
    @GetMapping("/myTasks")
    public Result<List<Map<String, Object>>> myTasks(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) List<String> roleKeys,
            @RequestParam(required = false) String roleKeysCsv) {
        return Result.ok(flowTaskService.listMyTasks(userId, roleKeys, roleKeysCsv));
    }

    /**
     * 完成审批（校验办理人身份；未签收则 claim，写入历史 assignee）。请求体仅支持 JSON。
     */
    @PostMapping("/complete")
    public Result<Boolean> completeTask(@Valid @RequestBody TaskCompleteDTO dto) {
        try {
            flowTaskService.completeTask(dto);
            return Result.ok(true, "审批完成");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail("审批失败: " + e.getMessage());
        }
    }

    /**
     * 查询审批进度
     */
    @GetMapping("/progress/{businessKey}")
    public Result<List<Map<String, Object>>> getProgress(@PathVariable String businessKey) {
        return Result.ok(flowTaskService.getProgress(businessKey));
    }
}
