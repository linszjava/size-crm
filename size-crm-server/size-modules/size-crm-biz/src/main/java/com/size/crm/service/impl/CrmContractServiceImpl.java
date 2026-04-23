package com.size.crm.service.impl;

import com.size.crm.domain.CrmContract;
import com.size.crm.mapper.CrmContractMapper;
import com.size.crm.service.ICrmContractService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * CrmContract Service 实现类
 */
@Service
public class CrmContractServiceImpl extends ServiceImpl<CrmContractMapper, CrmContract> implements ICrmContractService {
}
