import { BasicColumn, FormSchema } from '/@/components/Table';
import { h } from 'vue';
import { Tag } from 'ant-design-vue';

// 审核状态字典
export const AUDIT_STATUS_MAP = {
  'DRAFT': { label: '草稿', color: 'default' },
  'AUDITING': { label: '审批中', color: 'processing' },
  'APPROVED': { label: '已通过', color: 'success' },
  'REJECTED': { label: '已驳回', color: 'error' },
};

const auditStatusOptions = Object.entries(AUDIT_STATUS_MAP).map(([value, { label }]) => ({
  label,
  value,
}));

// 支付方式字典
const payTypeOptions = [
  { label: '银行转账', value: '银行转账' },
  { label: '支付宝', value: '支付宝' },
  { label: '微信', value: '微信' },
  { label: '现金', value: '现金' },
  { label: '支票', value: '支票' },
  { label: '其他', value: '其他' },
];

export const columns: BasicColumn[] = [
  {
    title: '回款编号',
    dataIndex: 'receivableNo',
    width: 150,
  },
  {
    title: '回款金额',
    dataIndex: 'amount',
    width: 120,
  },
  {
    title: '回款日期',
    dataIndex: 'returnDate',
    width: 120,
  },
  {
    title: '支付方式',
    dataIndex: 'payType',
    width: 100,
  },
  {
    title: '审批状态',
    dataIndex: 'auditStatus',
    width: 100,
    customRender: ({ record }) => {
      const info = AUDIT_STATUS_MAP[record.auditStatus];
      if (!info) return record.auditStatus || '-';
      return h(Tag, { color: info.color }, () => info.label);
    },
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 160,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'receivableNo',
    label: '回款编号',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'auditStatus',
    label: '审批状态',
    component: 'Select',
    componentProps: { options: auditStatusOptions },
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
    field: 'customerId',
    label: '客户ID',
    component: 'Input',
    required: true,
  },
  {
    field: 'contractId',
    label: '关联合同ID',
    component: 'Input',
    required: true,
  },
  {
    field: 'receivableNo',
    label: '回款编号',
    component: 'Input',
    required: true,
  },
  {
    field: 'amount',
    label: '回款金额',
    component: 'InputNumber',
    componentProps: {
      min: 0,
      precision: 2,
      style: { width: '100%' }
    },
    required: true,
  },
  {
    field: 'returnDate',
    label: '回款日期',
    component: 'DatePicker',
    componentProps: {
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
      style: { width: '100%' }
    },
    required: true,
  },
  {
    field: 'payType',
    label: '支付方式',
    component: 'Select',
    componentProps: { options: payTypeOptions },
    required: true,
  },
  {
    field: 'auditStatus',
    label: '审批状态',
    component: 'Select',
    componentProps: { options: auditStatusOptions, disabled: true },
    defaultValue: 'DRAFT',
    helpMessage: '初始状态为草稿，需通过列表的发起审批操作流转',
  },
  {
    field: 'remark',
    label: '备注',
    component: 'InputTextArea',
    componentProps: { rows: 4 },
  },
];
