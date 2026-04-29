package com.size.workflow.feign;

import com.size.common.core.domain.Result;
import com.size.common.workflow.WorkflowUserDisplayVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(contextId = "remoteInternalSysUser", value = "size-crm-system")
public interface RemoteInternalSysUserClient {

    @GetMapping("/internal/system/user/display/{id}")
    Result<WorkflowUserDisplayVO> getDisplay(@PathVariable("id") Long id);
}
