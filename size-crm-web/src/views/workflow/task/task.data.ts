import { BasicColumn } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '任务名称',
    dataIndex: 'taskName',
    width: 140,
  },
  {
    title: '审批类型',
    dataIndex: 'bizType',
    width: 110,
  },
  {
    title: '业务单号',
    dataIndex: 'bizNo',
    width: 150,
  },
  {
    title: '审批标题',
    dataIndex: 'bizTitle',
    width: 180,
  },
  {
    title: '金额',
    dataIndex: 'bizAmount',
    width: 120,
  },
  {
    title: '申请人',
    dataIndex: 'bizApplicant',
    width: 100,
  },
  {
    title: '业务摘要',
    dataIndex: 'bizSummary',
    width: 200,
  },
  {
    title: '流程实例ID',
    dataIndex: 'processInstanceId',
    width: 220,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
  },
];
