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

export const columns: BasicColumn[] = [
  {
    title: '合同编号',
    dataIndex: 'contractNo',
    width: 150,
  },
  {
    title: '合同名称',
    dataIndex: 'name',
    width: 200,
  },
  {
    title: '合同金额',
    dataIndex: 'totalAmount',
    width: 120,
  },
  {
    title: '签约日期',
    dataIndex: 'signDate',
    width: 120,
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
    field: 'name',
    label: '合同名称',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'contractNo',
    label: '合同编号',
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
    field: 'opportunityId',
    label: '关联商机ID',
    component: 'Input',
  },
  {
    field: 'name',
    label: '合同名称',
    component: 'Input',
    required: true,
  },
  {
    field: 'contractNo',
    label: '合同编号',
    component: 'Input',
    required: true,
  },
  {
    field: 'totalAmount',
    label: '合同总金额',
    component: 'InputNumber',
    componentProps: {
      min: 0,
      precision: 2,
      style: { width: '100%' }
    },
    required: true,
  },
  {
    field: 'signDate',
    label: '签约日期',
    component: 'DatePicker',
    componentProps: {
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
      style: { width: '100%' }
    },
  },
  {
    field: 'startDate',
    label: '开始日期',
    component: 'DatePicker',
    componentProps: {
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
      style: { width: '100%' }
    },
  },
  {
    field: 'endDate',
    label: '结束日期',
    component: 'DatePicker',
    componentProps: {
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
      style: { width: '100%' }
    },
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
