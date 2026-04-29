package com.size.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.size.common.core.domain.Result;
import com.size.common.workflow.WorkflowUserDisplayVO;
import com.size.system.model.vo.UserDetailVO;
import com.size.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微服务间调用：用户展示信息（不经由网关对外暴露路由时仍建议加网关注册隔离或签名校验）。
 */
@RestController
@RequestMapping("/internal/system/user")
@SaIgnore
public class InternalSysUserController {

    @Autowired
    private ISysUserService userService;

    /**
     * 工作流等模块解析办理人/负责人显示名
     */
    @GetMapping("/display/{id}")
    public Result<WorkflowUserDisplayVO> display(@PathVariable Long id) {
        UserDetailVO detail = userService.getUserDetail(id);
        if (detail == null) {
            return Result.ok(null);
        }
        WorkflowUserDisplayVO vo = new WorkflowUserDisplayVO();
        vo.setNickname(detail.getNickname());
        vo.setUsername(detail.getUsername());
        return Result.ok(vo);
    }
}
