package com.size.workflow.controller;

import com.size.common.core.domain.Result;
import com.size.workflow.service.IFlowDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 流程定义（BPMN）查询 HTTP 入口
 */
@RestController
@RequestMapping("/workflow/definition")
public class FlowDefinitionController {

    @Autowired
    private IFlowDefinitionService flowDefinitionService;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        return Result.ok(flowDefinitionService.listLatestDefinitions());
    }

    @GetMapping("/xml/{definitionId}")
    public Result<String> xml(@PathVariable String definitionId) {
        try {
            return Result.ok(flowDefinitionService.getBpmnXml(definitionId));
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail("读取流程XML失败: " + e.getMessage());
        }
    }
}
