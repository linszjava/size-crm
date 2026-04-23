import type { AppRouteModule } from '/@/router/types';

import { LAYOUT } from '/@/router/constant';

const crm: AppRouteModule = {
  path: '/crm',
  name: 'Crm',
  component: LAYOUT,
  redirect: '/crm/customer',
  meta: {
    orderNo: 20,
    icon: 'ion:briefcase-outline',
    title: 'CRM系统',
  },
  children: [
    {
      path: 'customer',
      name: 'CrmCustomer',
      component: () => import('/@/views/crm/customer/index.vue'),
      meta: {
        title: '客户管理',
      },
    },
    {
      path: 'leads',
      name: 'CrmLeads',
      component: () => import('/@/views/crm/leads/index.vue'),
      meta: {
        title: '线索池',
      },
    },
    {
      path: 'contact',
      name: 'CrmContact',
      component: () => import('/@/views/crm/contact/index.vue'),
      meta: {
        title: '联系人',
      },
    },
    {
      path: 'opportunity',
      name: 'CrmOpportunity',
      component: () => import('/@/views/crm/opportunity/index.vue'),
      meta: {
        title: '商机',
      },
    },
    {
      path: 'contract',
      name: 'CrmContract',
      component: () => import('/@/views/crm/contract/index.vue'),
      meta: {
        title: '合同管理',
      },
    },
    {
      path: 'receivable',
      name: 'CrmReceivable',
      component: () => import('/@/views/crm/receivable/index.vue'),
      meta: {
        title: '回款管理',
      },
    },
    {
      path: 'follow',
      name: 'CrmFollow',
      component: () => import('/@/views/crm/follow/index.vue'),
      meta: {
        title: '跟进记录',
      },
    },
  ],
};

export default crm;
