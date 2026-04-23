import { BasicColumn, FormSchema } from '/@/components/Table';
import { Tag } from 'ant-design-vue';
import { h } from 'vue';

/** 表格列定义 */
export const columns: BasicColumn[] = [
  {
    title: '姓名',
    dataIndex: 'name',
    width: 100,
  },
  {
    title: '职务',
    dataIndex: 'post',
    width: 120,
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    width: 130,
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    width: 180,
  },
  {
    title: '微信号',
    dataIndex: 'wechat',
    width: 130,
  },
  {
    title: '关键决策人',
    dataIndex: 'isPrimary',
    width: 110,
    customRender: ({ record }) => {
      return record.isPrimary === 1
        ? h(Tag, { color: 'red' }, () => '关键决策人')
        : h(Tag, { color: 'default' }, () => '普通联系人');
    },
  },
  {
    title: '下次跟进',
    dataIndex: 'nextFollowTime',
    width: 160,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 160,
  },
];

/** 搜索表单 */
export const searchFormSchema: FormSchema[] = [
  {
    field: 'name',
    label: '姓名',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'phone',
    label: '手机号',
    component: 'Input',
    colProps: { span: 6 },
  },
];

/** 新增/编辑表单 */
export const formSchema: FormSchema[] = [
  {
    field: 'id',
    label: 'id',
    component: 'Input',
    show: false,
  },
  {
    field: 'name',
    label: '联系人姓名',
    component: 'Input',
    required: true,
  },
  {
    field: 'post',
    label: '职务',
    component: 'Input',
  },
  {
    field: 'phone',
    label: '手机号',
    component: 'Input',
    required: true,
  },
  {
    field: 'telephone',
    label: '座机',
    component: 'Input',
  },
  {
    field: 'email',
    label: '邮箱',
    component: 'Input',
  },
  {
    field: 'wechat',
    label: '微信号',
    component: 'Input',
  },
  {
    field: 'isPrimary',
    label: '是否关键决策人',
    component: 'RadioButtonGroup',
    defaultValue: 0,
    componentProps: {
      options: [
        { label: '否', value: 0 },
        { label: '是', value: 1 },
      ],
    },
  },
  {
    field: 'nextFollowTime',
    label: '下次跟进时间',
    component: 'DatePicker',
    componentProps: {
      showTime: true,
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
    },
  },
  {
    field: 'address',
    label: '地址',
    component: 'Input',
  },
  {
    field: 'remark',
    label: '备注',
    component: 'InputTextArea',
    componentProps: { rows: 3 },
  },
];
