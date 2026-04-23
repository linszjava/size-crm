package com.size.crm.service.impl;

import com.size.crm.domain.CrmCustomer;
import com.size.crm.mapper.CrmCustomerMapper;
import com.size.crm.service.ICrmCustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * CrmCustomer Service 实现类
 */
@Service
public class CrmCustomerServiceImpl extends ServiceImpl<CrmCustomerMapper, CrmCustomer> implements ICrmCustomerService {
}
