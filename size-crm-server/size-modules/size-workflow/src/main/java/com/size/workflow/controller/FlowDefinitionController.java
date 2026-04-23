package com.size.workflow.controller;

import com.size.common.core.domain.Result;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 流程定义（BPMN）查询接口
 */
@RestController
@RequestMapping("/workflow/definition")
public class FlowDefinitionController {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 获取流程定义列表（仅 latest 版本）
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .orderByProcessDefinitionKey().asc()
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
        return Result.ok(result);
    }

    /**
     * 获取 BPMN XML（用于前端渲染流程图）
     */
    @GetMapping("/xml/{definitionId}")
    public Result<String> xml(@PathVariable String definitionId) {
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult();
        if (pd == null) {
            return Result.fail("流程定义不存在");
        }

        try (InputStream in = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getResourceName())) {
            if (in == null) {
                return Result.fail("未找到流程资源文件");
            }
            String xml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return Result.ok(xml);
        } catch (Exception e) {
            return Result.fail("读取流程XML失败: " + e.getMessage());
        }
    }
}

