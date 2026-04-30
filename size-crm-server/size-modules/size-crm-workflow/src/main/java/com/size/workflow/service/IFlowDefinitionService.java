package com.size.workflow.service;

import java.util.List;
import java.util.Map;

/**
 * 流程定义（Camunda Repository）查询
 */
public interface IFlowDefinitionService {

    List<Map<String, Object>> listLatestDefinitions();

    /**
     * BPMN XML；不存在时抛出 {@link IllegalArgumentException}；读取失败抛出 {@link IllegalStateException}。
     */
    String getBpmnXml(String definitionId);
}
