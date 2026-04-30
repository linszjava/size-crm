package com.size.workflow.service;

import com.size.workflow.model.dto.TaskCompleteDTO;

import java.util.List;
import java.util.Map;

/**
 * 工作流任务（Camunda）应用服务
 */
public interface IFlowTaskService {

    String startProcess(String processDefinitionKey, String businessKey, String userId);

    List<Map<String, Object>> listMyTasks(String userId, List<String> roleKeys, String roleKeysCsv);

    /**
     * 完成任务；业务校验失败抛出 {@link IllegalArgumentException}，消息可直接展示给前端。
     */
    void completeTask(TaskCompleteDTO dto);

    List<Map<String, Object>> getProgress(String businessKey);
}
