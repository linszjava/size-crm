import type { BasicColumn } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: 'Key',
    dataIndex: 'key',
    width: 160,
  },
  {
    title: '名称',
    dataIndex: 'name',
    width: 220,
  },
  {
    title: '版本',
    dataIndex: 'version',
    width: 80,
  },
  {
    title: '资源文件',
    dataIndex: 'resourceName',
    width: 260,
  },
  {
    title: '定义ID',
    dataIndex: 'definitionId',
    width: 260,
  },
];

