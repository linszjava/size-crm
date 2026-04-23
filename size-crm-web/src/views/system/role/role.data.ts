import { BasicColumn, FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '角色名称',
    dataIndex: 'roleName',
    width: 200,
  },
  {
    title: '权限字符',
    dataIndex: 'roleKey',
    width: 180,
  },
  {
    title: '显示顺序',
    dataIndex: 'roleSort',
    width: 100,
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
    field: 'roleName',
    label: '角色名称',
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    field: 'roleKey',
    label: '权限字符',
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
    field: 'roleName',
    label: '角色名称',
    component: 'Input',
    required: true,
  },
  {
    field: 'roleKey',
    label: '权限字符',
    component: 'Input',
    required: true,
  },
  {
    field: 'roleSort',
    label: '显示顺序',
    component: 'InputNumber',
    required: true,
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
