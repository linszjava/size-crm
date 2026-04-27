package com.size.api;

import com.size.api.domain.LoginUser;
import com.size.api.domain.UserRegisterDTO;
import com.size.api.domain.UserPermissionInfo;
import com.size.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 远程用户服务
 */
@FeignClient(contextId = "remoteUserService", value = "size-system")
public interface RemoteUserService {

    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @return 结果
     */
    @GetMapping("/system/user/info/{username}")
    Result<LoginUser> getUserInfo(@PathVariable("username") String username);

    /**
     * 通过用户ID获取权限聚合数据
     *
     * @param userId 用户ID
     * @return 结果
     */
    @GetMapping("/system/user/permission/{userId}")
    Result<UserPermissionInfo> getUserPermission(@PathVariable("userId") Long userId);

    /**
     * 用户注册
     *
     * @param dto 注册参数
     * @return 结果
     */
    @PostMapping("/system/user/register")
    Result<Boolean> register(@RequestBody UserRegisterDTO dto);
}
