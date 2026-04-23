import { BasicColumn, FormSchema } from '/@/components/Table';
import { Tag } from 'ant-design-vue';
import { h } from 'vue';
import {
  LEADS_STATUS_MAP,
  LEADS_SOURCE_MAP,
  LEADS_INTENTION_MAP,
} from '/@/api/crm/model/leadsModel';

/** 状态下拉选项 */
const statusOptions = Object.entries(LEADS_STATUS_MAP).map(([value, { label }]) => ({
  label,
  value,
}));

/** 来源下拉选项 */
const sourceOptions = Object.entries(LEADS_SOURCE_MAP).map(([value, label]) => ({
  label,
  value,
}));

/** 意向度下拉选项 */
const intentionOptions = Object.entries(LEADS_INTENTION_MAP).map(([value, { label }]) => ({
  label,
  value,
}));

/** 表格列定义 */
export const columns: BasicColumn[] = [
  {
    title: '线索姓名',
    dataIndex: 'name',
    width: 100,
  },
  {
    title: '联系电话',
    dataIndex: 'phone',
    width: 130,
  },
  {
    title: '所在公司',
    dataIndex: 'companyName',
    width: 180,
  },
  {
    title: '来源',
    dataIndex: 'source',
    width: 100,
    customRender: ({ record }) => LEADS_SOURCE_MAP[record.source] || record.source,
  },
  {
    title: '意向度',
    dataIndex: 'intention',
    width: 90,
    customRender: ({ record }) => {
      const info = LEADS_INTENTION_MAP[record.intention];
      if (!info) return record.intention || '-';
      return h(Tag, { color: info.color }, () => info.label);
    },
  },
  {
    title: '线索状态',
    dataIndex: 'status',
    width: 100,
    customRender: ({ record }) => {
      const info = LEADS_STATUS_MAP[record.status];
      if (!info) return record.status || '-';
      return h(Tag, { color: info.color }, () => info.label);
    },
  },
  {
    title: '下次跟进时间',
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
    label: '线索姓名',
    component: 'Input',
    colProps: { span: 5 },
  },
  {
    field: 'phone',
    label: '联系电话',
    component: 'Input',
    colProps: { span: 5 },
  },
  {
    field: 'status',
    label: '线索状态',
    component: 'Select',
    componentProps: { options: statusOptions },
    colProps: { span: 4 },
  },
  {
    field: 'source',
    label: '线索来源',
    component: 'Select',
    componentProps: { options: sourceOptions },
    colProps: { span: 4 },
  },
  {
    field: 'intention',
    label: '意向度',
    component: 'Select',
    componentProps: { options: intentionOptions },
    colProps: { span: 4 },
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
    label: '线索姓名',
    component: 'Input',
    required: true,
  },
  {
    field: 'phone',
    label: '联系电话',
    component: 'Input',
    required: true,
  },
  {
    field: 'wechat',
    label: '微信号',
    component: 'Input',
  },
  {
    field: 'email',
    label: '邮箱',
    component: 'Input',
  },
  {
    field: 'companyName',
    label: '所在公司',
    component: 'Input',
  },
  {
    field: 'position',
    label: '职位',
    component: 'Input',
  },
  {
    field: 'industry',
    label: '所属行业',
    component: 'Input',
  },
  {
    field: 'source',
    label: '线索来源',
    component: 'Select',
    componentProps: { options: sourceOptions },
  },
  {
    field: 'intention',
    label: '意向度',
    component: 'Select',
    componentProps: { options: intentionOptions },
  },
  {
    field: 'status',
    label: '线索状态',
    component: 'Select',
    componentProps: { options: statusOptions },
    defaultValue: 'UNASSIGNED',
  },
  {
    field: 'nextFollowTime',
    label: '下次跟进',
    component: 'DatePicker',
    componentProps: {
      showTime: true,
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
    },
  },
  {
    field: 'remark',
    label: '备注',
    component: 'InputTextArea',
    componentProps: { rows: 3 },
  },
];
