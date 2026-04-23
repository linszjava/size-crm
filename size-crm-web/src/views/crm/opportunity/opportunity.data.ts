import { BasicColumn, FormSchema } from '/@/components/Table';
import { h } from 'vue';
import { Tag } from 'ant-design-vue';

// 阶段字典
export const STAGE_MAP = {
  '需求发现': { label: '需求发现', color: 'blue' },
  '方案提供': { label: '方案提供', color: 'cyan' },
  '商务谈判': { label: '商务谈判', color: 'orange' },
  '赢单': { label: '赢单', color: 'green' },
  '输单': { label: '输单', color: 'red' },
};

const stageOptions = Object.entries(STAGE_MAP).map(([value, { label }]) => ({
  label,
  value,
}));

export const columns: BasicColumn[] = [
  {
    title: '商机名称',
    dataIndex: 'name',
    width: 200,
  },
  {
    title: '预计销售金额',
    dataIndex: 'expectedAmount',
    width: 150,
  },
  {
    title: '预计签单日期',
    dataIndex: 'expectedDate',
    width: 150,
  },
  {
    title: '销售阶段',
    dataIndex: 'salesStage',
    width: 120,
    customRender: ({ record }) => {
      const info = STAGE_MAP[record.salesStage];
      if (!info) return record.salesStage || '-';
      return h(Tag, { color: info.color }, () => info.label);
    },
  },
  {
    title: '赢单率(%)',
    dataIndex: 'winRate',
    width: 100,
    customRender: ({ record }) => {
      return record.winRate ? `${record.winRate}%` : '-';
    }
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
    label: '商机名称',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'salesStage',
    label: '销售阶段',
    component: 'Select',
    componentProps: { options: stageOptions },
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
    field: 'name',
    label: '商机名称',
    component: 'Input',
    required: true,
  },
  {
    field: 'expectedAmount',
    label: '预计销售金额',
    component: 'InputNumber',
    componentProps: {
      min: 0,
      precision: 2,
      style: { width: '100%' }
    },
    required: true,
  },
  {
    field: 'expectedDate',
    label: '预计签单日期',
    component: 'DatePicker',
    componentProps: {
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
      style: { width: '100%' }
    },
  },
  {
    field: 'salesStage',
    label: '销售阶段',
    component: 'Select',
    componentProps: { options: stageOptions },
  },
  {
    field: 'winRate',
    label: '赢单率(%)',
    component: 'InputNumber',
    componentProps: {
      min: 0,
      max: 100,
      precision: 2,
      style: { width: '100%' }
    },
  },
  {
    field: 'remark',
    label: '备注',
    component: 'InputTextArea',
    componentProps: { rows: 4 },
  },
];
