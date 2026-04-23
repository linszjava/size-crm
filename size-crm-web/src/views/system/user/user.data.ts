import { BasicColumn, FormSchema } from '/@/components/Table';
import { getDeptList } from '/@/api/system/dept';
import { getRoleList } from '/@/api/system/role';

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
      api: getDeptList,
      fieldNames: {
        label: 'deptName',
        key: 'id',
        value: 'id',
      },
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
