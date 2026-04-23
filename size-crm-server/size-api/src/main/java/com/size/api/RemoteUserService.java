package com.size.api;

import com.size.api.domain.LoginUser;
import com.size.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}
