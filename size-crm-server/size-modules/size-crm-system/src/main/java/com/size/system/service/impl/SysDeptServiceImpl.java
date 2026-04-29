package com.size.system.service.impl;

import com.size.system.domain.SysDept;
import com.size.system.mapper.SysDeptMapper;
import com.size.system.service.ISysDeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * SysDept Service 实现类
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {
}
