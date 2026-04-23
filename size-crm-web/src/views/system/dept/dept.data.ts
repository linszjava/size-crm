import { BasicColumn, FormSchema } from '/@/components/Table';
import { getDeptList } from '/@/api/system/dept';

export const columns: BasicColumn[] = [
  {
    title: '部门名称',
    dataIndex: 'deptName',
    width: 200,
    align: 'left',
  },
  {
    title: '排序',
    dataIndex: 'orderNum',
    width: 80,
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
    field: 'deptName',
    label: '部门名称',
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
    field: 'parentId',
    label: '上级部门',
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
  },
  {
    field: 'deptName',
    label: '部门名称',
    component: 'Input',
    required: true,
  },
  {
    field: 'orderNum',
    label: '显示排序',
    component: 'InputNumber',
    required: true,
  },
  {
    field: 'leader',
    label: '负责人',
    component: 'Input',
  },
  {
    field: 'phone',
    label: '联系电话',
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
