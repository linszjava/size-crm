package com.size.system.service.impl;

import com.size.system.domain.SysUser;
import com.size.system.mapper.SysUserMapper;
import com.size.system.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * SysUser Service 实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
}
