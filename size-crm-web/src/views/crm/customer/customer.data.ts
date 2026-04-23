import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '客户名称',
    dataIndex: 'name',
    width: 150,
  },
  {
    title: '客户级别',
    dataIndex: 'level',
    width: 100,
  },
  {
    title: '联系电话',
    dataIndex: 'phone',
    width: 120,
  },
  {
    title: '所属行业',
    dataIndex: 'industry',
    width: 100,
  },
  {
    title: '成交状态',
    dataIndex: 'dealStatus',
    width: 100,
    customRender: ({ record }) => {
      return record.dealStatus === 1 ? '已成交' : '未成交';
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
    field: 'name',
    label: '客户名称',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'phone',
    label: '联系电话',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'level',
    label: '客户级别',
    component: 'Select',
    componentProps: {
      options: [
        { label: 'VIP客户', value: 'VIP' },
        { label: '普通客户', value: 'NORMAL' },
      ],
    },
    colProps: { span: 6 },
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
    field: 'name',
    label: '客户名称',
    component: 'Input',
    required: true,
  },
  {
    field: 'level',
    label: '客户级别',
    component: 'Select',
    componentProps: {
      options: [
        { label: 'VIP客户', value: 'VIP' },
        { label: '普通客户', value: 'NORMAL' },
      ],
    },
    required: true,
  },
  {
    field: 'phone',
    label: '联系电话',
    component: 'Input',
  },
  {
    field: 'industry',
    label: '所属行业',
    component: 'Input',
  },
  {
    field: 'address',
    label: '详细地址',
    component: 'InputTextArea',
  },
  {
    field: 'remark',
    label: '备注',
    component: 'InputTextArea',
  },
];
