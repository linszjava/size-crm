package com.size.workflow.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 完成任务接口 JSON 请求体（仅 application/json）。
 */
public record TaskCompleteDTO(
        @NotBlank(message = "参数缺失: taskId/approved")
        String taskId,
        @NotNull(message = "参数缺失: taskId/approved")
        Boolean approved,
        String comment,
        @NotBlank(message = "参数缺失: userId（用于校验办理权限）")
        String userId,
        List<String> roleKeys
) {
}
