package com.size.workflow.service.impl;

import com.size.workflow.service.IFlowDefinitionService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FlowDefinitionServiceImpl implements IFlowDefinitionService {

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public List<Map<String, Object>> listLatestDefinitions() {
        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .orderByProcessDefinitionKey()
                .asc()
                .list();

        List<Map<String, Object>> result = new ArrayList<>();
        for (ProcessDefinition pd : definitions) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("definitionId", pd.getId());
            map.put("key", pd.getKey());
            map.put("name", pd.getName());
            map.put("version", pd.getVersion());
            map.put("deploymentId", pd.getDeploymentId());
            map.put("resourceName", pd.getResourceName());
            map.put("tenantId", pd.getTenantId());
            result.add(map);
        }
        return result;
    }

    @Override
    public String getBpmnXml(String definitionId) {
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult();
        if (pd == null) {
            throw new IllegalArgumentException("流程定义不存在");
        }
        try (InputStream in = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getResourceName())) {
            if (in == null) {
                throw new IllegalArgumentException("未找到流程资源文件");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("读取流程XML失败: " + e.getMessage(), e);
        }
    }
}
