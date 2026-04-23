package com.size.crm.service.impl;

import com.size.crm.domain.CrmLeads;
import com.size.crm.mapper.CrmLeadsMapper;
import com.size.crm.service.ICrmLeadsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * CrmLeads Service 实现类
 */
@Service
public class CrmLeadsServiceImpl extends ServiceImpl<CrmLeadsMapper, CrmLeads> implements ICrmLeadsService {
}
