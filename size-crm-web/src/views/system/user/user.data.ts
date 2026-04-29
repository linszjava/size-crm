import { BasicColumn, FormSchema } from '/@/components/Table';
import { h } from 'vue';
import { Space, Tag } from 'ant-design-vue';
import { getDeptList } from '/@/api/system/dept';
import { getRoleList } from '/@/api/system/role';

function buildDeptTree(flatList: Recordable[] = []): Recordable[] {
  const nodeMap = new Map<string, Recordable>();
  const roots: Recordable[] = [];

  (flatList || []).forEach((item) => {
    const key = String(item.id);
    nodeMap.set(key, {
      title: item.deptName,
      value: String(item.id),
      key,
      orderNum: item.orderNum ?? 0,
      children: [],
    });
  });

  (flatList || []).forEach((item) => {
    const current = nodeMap.get(String(item.id));
    if (!current) return;
    const parentId = item.parentId;
    const parentKey = parentId === null || parentId === undefined ? '' : String(parentId);
    if (!parentKey || parentKey === '0' || !nodeMap.has(parentKey)) {
      roots.push(current);
      return;
    }
    const parent = nodeMap.get(parentKey);
    if (!parent) return;
    (parent.children || []).push(current);
  });

  const sortNodes = (nodes: Recordable[]) => {
    nodes.sort((a, b) => Number(a.orderNum || 0) - Number(b.orderNum || 0));
    nodes.forEach((node) => {
      if (node.children?.length) {
        sortNodes(node.children);
      }
    });
  };
  sortNodes(roots);
  return roots;
}

async function getDeptTreeOptions() {
  const list = await getDeptList();
  return buildDeptTree(list || []);
}

async function getRoleOptions() {
  const list = await getRoleList();
  return (list || []).map((item) => ({
    label: item.roleName,
    value: String(item.id),
  }));
}

export const columns: BasicColumn[] = [
  {
    title: '用户账号',
    dataIndex: 'username',
    width: 120,
  },
  {
    title: '用户昵称',
    dataIndex: 'nickname',
    width: 120,
  },
  {
    title: '角色',
    dataIndex: 'roleNames',
    width: 240,
    align: 'left',
    customRender: ({ record }) => {
      const names = (record.roleNames || []) as string[];
      if (!names.length) {
        return h('span', { class: 'text-gray-400' }, '—');
      }
      return h(
        Space,
        { size: [4, 4], wrap: true, class: 'py-1' },
        () =>
          names.map((name, idx) =>
            h(Tag, { color: 'blue', key: `${name}-${idx}` }, () => name),
          ),
      );
    },
  },
  {
    title: '部门ID', // TODO: should be mapped to Dept Name in real app
    dataIndex: 'deptId',
    width: 120,
  },
  {
    title: '手机号码',
    dataIndex: 'phonenumber',
    width: 120,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
    customRender: ({ record }) => {
      return record.status === 0 ? '正常' : '停用';
    },
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'username',
    label: '用户账号',
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    field: 'phonenumber',
    label: '手机号码',
    component: 'Input',
    colProps: { span: 8 },
  },
];

export const formSchema: FormSchema[] = [
  {
    field: 'id',
    label: 'id',
    component: 'Input',
    show: false,
  },
  {
    field: 'username',
    label: '用户账号',
    component: 'Input',
    required: true,
  },
  {
    field: 'nickname',
    label: '用户昵称',
    component: 'Input',
    required: true,
  },
  {
    field: 'password',
    label: '密码',
    component: 'InputPassword',
    required: true,
    show: ({ values }) => !values.id,
  },
  {
    field: 'deptId',
    label: '归属部门',
    component: 'ApiTreeSelect',
    componentProps: {
      api: getDeptTreeOptions,
      getPopupContainer: () => document.body,
    },
    required: true,
  },
  {
    field: 'roleIds',
    label: '角色',
    component: 'ApiSelect',
    componentProps: {
      api: getRoleOptions,
      mode: 'multiple',
      getPopupContainer: () => document.body,
    },
    required: true,
  },
  {
    field: 'phonenumber',
    label: '手机号码',
    component: 'Input',
  },
  {
    field: 'email',
    label: '邮箱',
    component: 'Input',
  },
  {
    field: 'status',
    label: '状态',
    component: 'RadioButtonGroup',
    defaultValue: 0,
    componentProps: {
      options: [
        { label: '正常', value: 0 },
        { label: '停用', value: 1 },
      ],
    },
  },
];
