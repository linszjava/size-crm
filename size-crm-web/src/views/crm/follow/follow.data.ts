import { BasicColumn, FormSchema } from '/@/components/Table';

export const FOLLOW_TYPE_OPTIONS = [
  { label: '电话', value: 'PHONE' },
  { label: '微信', value: 'WECHAT' },
  { label: '拜访', value: 'VISIT' },
  { label: '邮件', value: 'EMAIL' },
  { label: '会议', value: 'MEETING' },
  { label: '其他', value: 'OTHER' },
];

export const BIZ_TYPE_OPTIONS = [
  { label: '客户', value: 'CUSTOMER' },
  { label: '线索', value: 'LEADS' },
  { label: '联系人', value: 'CONTACT' },
  { label: '商机', value: 'OPPORTUNITY' },
  { label: '合同', value: 'CONTRACT' },
  { label: '回款', value: 'RECEIVABLE' },
];

export const columns: BasicColumn[] = [
  {
    title: '业务类型',
    dataIndex: 'bizType',
    width: 120,
  },
  {
    title: '业务ID',
    dataIndex: 'bizId',
    width: 120,
  },
  {
    title: '跟进方式',
    dataIndex: 'followType',
    width: 120,
  },
  {
    title: '跟进内容',
    dataIndex: 'content',
    width: 300,
  },
  {
    title: '下次联系时间',
    dataIndex: 'nextTime',
    width: 180,
  },
  {
    title: '记录人ID',
    dataIndex: 'recordUserId',
    width: 120,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'bizType',
    label: '业务类型',
    component: 'Select',
    componentProps: { options: BIZ_TYPE_OPTIONS },
    colProps: { span: 6 },
  },
  {
    field: 'bizId',
    label: '业务ID',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'followType',
    label: '跟进方式',
    component: 'Select',
    componentProps: { options: FOLLOW_TYPE_OPTIONS },
    colProps: { span: 6 },
  },
  {
    field: 'content',
    label: '内容关键字',
    component: 'Input',
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
    field: 'bizType',
    label: '业务类型',
    component: 'Select',
    componentProps: { options: BIZ_TYPE_OPTIONS },
    required: true,
  },
  {
    field: 'bizId',
    label: '业务ID',
    component: 'Select',
    componentProps: {
      options: [],
      showSearch: true,
      optionFilterProp: 'label',
      placeholder: '请先选择业务类型',
    },
    required: true,
  },
  {
    field: 'followType',
    label: '跟进方式',
    component: 'Select',
    componentProps: { options: FOLLOW_TYPE_OPTIONS },
    required: true,
  },
  {
    field: 'content',
    label: '跟进内容',
    component: 'InputTextArea',
    componentProps: { rows: 4, maxlength: 1000 },
    required: true,
  },
  {
    field: 'nextTime',
    label: '下次联系时间',
    component: 'DatePicker',
    componentProps: {
      showTime: true,
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
      style: { width: '100%' },
    },
  },
  {
    field: 'recordUserId',
    label: '记录人ID',
    component: 'Select',
    componentProps: {
      options: [],
      showSearch: true,
      optionFilterProp: 'label',
    },
    required: true,
  },
];

