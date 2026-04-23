import type { AppRouteModule } from '/@/router/types';

import { LAYOUT } from '/@/router/constant';

const system: AppRouteModule = {
  path: '/system',
  name: 'System',
  component: LAYOUT,
  redirect: '/system/user',
  meta: {
    orderNo: 2000,
    icon: 'ion:settings-outline',
    title: '系统管理',
  },
  children: [
    {
      path: 'user',
      name: 'UserManagement',
      meta: {
        title: '用户管理',
        ignoreKeepAlive: false,
      },
      component: () => import('/@/views/system/user/index.vue'),
    },
    {
      path: 'role',
      name: 'RoleManagement',
      meta: {
        title: '角色管理',
        ignoreKeepAlive: false,
      },
      component: () => import('/@/views/system/role/index.vue'),
    },
    {
      path: 'dept',
      name: 'DeptManagement',
      meta: {
        title: '部门管理',
        ignoreKeepAlive: false,
      },
      component: () => import('/@/views/system/dept/index.vue'),
    },
    {
      path: 'menu',
      name: 'MenuManagement',
      meta: {
        title: '菜单管理',
        ignoreKeepAlive: false,
      },
      component: () => import('/@/views/system/menu/index.vue'),
    },
  ],
};

export default system;
