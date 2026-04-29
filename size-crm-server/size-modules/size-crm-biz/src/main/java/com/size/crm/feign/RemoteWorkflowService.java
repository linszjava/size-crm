package com.size.crm.feign;

import com.size.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteWorkflowService", value = "size-crm-workflow")
public interface RemoteWorkflowService {

    /**
     * 发起流程
     *
     * @param processDefinitionKey 流程定义键
     * @param businessKey          业务ID
     * @param userId               发起人ID
     * @return 流程实例ID
     */
    @PostMapping("/workflow/task/start")
    Result<String> startProcess(@RequestParam("processDefinitionKey") String processDefinitionKey,
                                @RequestParam("businessKey") String businessKey,
                                @RequestParam("userId") String userId);
}
