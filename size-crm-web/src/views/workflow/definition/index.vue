<template>
  <div>
    <BasicTable @register="registerTable">
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              label: '查看流程图',
              icon: 'ion:layers-outline',
              onClick: handleView.bind(null, record),
            },
          ]"
        />
      </template>
    </BasicTable>

    <DefinitionModal @register="registerModal" />
  </div>
</template>

<script lang="ts" setup>
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { getProcessDefinitionList } from '/@/api/workflow/definition';
  import DefinitionModal from './DefinitionModal.vue';
  import { columns } from './definition.data';

  const [registerModal, { openModal }] = useModal();

  const [registerTable] = useTable({
    title: '流程定义列表',
    api: getProcessDefinitionList,
    columns,
    pagination: false,
    showTableSetting: true,
    bordered: true,
    showIndexColumn: false,
    actionColumn: {
      width: 140,
      title: '操作',
      dataIndex: 'action',
      slots: { customRender: 'action' },
      fixed: undefined,
    },
  });

  function handleView(record: Recordable) {
    openModal(true, { definitionId: record.definitionId });
  }
</script>

