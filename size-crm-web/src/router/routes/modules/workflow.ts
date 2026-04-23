import type { AppRouteModule } from '/@/router/types';

import { LAYOUT } from '/@/router/constant';

const workflow: AppRouteModule = {
  path: '/workflow',
  name: 'Workflow',
  component: LAYOUT,
  redirect: '/workflow/task',
  meta: {
    orderNo: 2100,
    icon: 'ion:layers-outline',
    title: '流程审批',
  },
  children: [
    {
      path: 'task',
      name: 'MyTasks',
      meta: {
        title: '我的待办',
        ignoreKeepAlive: false,
      },
      component: () => import('/@/views/workflow/task/index.vue'),
    },
  ],
};

export default workflow;
