package com.size.crm.service.impl;

import com.size.crm.domain.CrmContact;
import com.size.crm.mapper.CrmContactMapper;
import com.size.crm.service.ICrmContactService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * CrmContact Service 实现类
 */
@Service
public class CrmContactServiceImpl extends ServiceImpl<CrmContactMapper, CrmContact> implements ICrmContactService {
}
