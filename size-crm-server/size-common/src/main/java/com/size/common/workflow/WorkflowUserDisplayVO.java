package com.size.common.workflow;

import lombok.Data;

import java.io.Serializable;

/**
 * 工作流展示用用户简要信息（跨模块 Feign 传输）
 */
@Data
public class WorkflowUserDisplayVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nickname;
    private String username;
}
