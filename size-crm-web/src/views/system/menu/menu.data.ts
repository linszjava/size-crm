import { BasicColumn, FormSchema } from '/@/components/Table';
import { h } from 'vue';
import { Tag } from 'ant-design-vue';
import { Icon } from '/@/components/Icon';
import { getMenuList } from '/@/api/system/menu';

export const columns: BasicColumn[] = [
  {
    title: '菜单名称',
    dataIndex: 'menuName',
    width: 200,
    align: 'left',
  },
  {
    title: '图标',
    dataIndex: 'icon',
    width: 50,
    customRender: ({ record }) => {
      return record.icon ? h(Icon, { icon: record.icon }) : '';
    },
  },
  {
    title: '权限标识',
    dataIndex: 'perms',
    width: 180,
  },
  {
    title: '菜单类型',
    dataIndex: 'menuType',
    width: 100,
    customRender: ({ record }) => {
      const typeMap: Record<string, string> = {
        M: '目录',
        C: '菜单',
        F: '按钮',
      };
      return typeMap[record.menuType] || '-';
    },
  },
  {
    title: '组件路径',
    dataIndex: 'component',
    width: 180,
  },
  {
    title: '排序',
    dataIndex: 'orderNum',
    width: 50,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 80,
    customRender: ({ record }) => {
      const status = record.status;
      const enable = status === 0;
      const color = enable ? 'green' : 'red';
      const text = enable ? '正常' : '停用';
      return h(Tag, { color: color }, () => text);
    },
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'menuName',
    label: '菜单名称',
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
    label: '上级菜单',
    component: 'ApiTreeSelect',
    componentProps: {
      api: getMenuList,
      fieldNames: {
        label: 'menuName',
        key: 'id',
        value: 'id',
      },
      getPopupContainer: () => document.body,
    },
  },
  {
    field: 'menuName',
    label: '菜单名称',
    component: 'Input',
    required: true,
  },
  {
    field: 'menuType',
    label: '菜单类型',
    component: 'RadioButtonGroup',
    defaultValue: 'C',
    required: true,
    componentProps: {
      options: [
        { label: '目录', value: 'M' },
        { label: '菜单', value: 'C' },
        { label: '按钮', value: 'F' },
      ],
    },
  },
  {
    field: 'orderNum',
    label: '显示排序',
    component: 'InputNumber',
    required: true,
  },
  {
    field: 'icon',
    label: '图标',
    component: 'IconPicker',
  },
  {
    field: 'path',
    label: '路由地址',
    component: 'Input',
    ifShow: ({ values }) => values.menuType !== 'F',
    required: true,
  },
  {
    field: 'component',
    label: '组件路径',
    component: 'Input',
    ifShow: ({ values }) => values.menuType === 'C',
    required: true,
  },
  {
    field: 'perms',
    label: '权限字符',
    component: 'Input',
    ifShow: ({ values }) => values.menuType === 'F',
    required: true,
  },
  {
    field: 'visible',
    label: '显示状态',
    component: 'RadioButtonGroup',
    defaultValue: 1,
    componentProps: {
      options: [
        { label: '显示', value: 1 },
        { label: '隐藏', value: 0 },
      ],
    },
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
