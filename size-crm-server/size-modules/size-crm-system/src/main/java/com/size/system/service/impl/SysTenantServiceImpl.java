package com.size.system.service.impl;

import com.size.system.domain.SysTenant;
import com.size.system.mapper.SysTenantMapper;
import com.size.system.service.ISysTenantService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * SysTenant Service 实现类
 */
@Service
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant> implements ISysTenantService {
}
