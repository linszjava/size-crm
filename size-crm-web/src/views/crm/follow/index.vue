<template>
  <div>
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleCreate">新增跟进</a-button>
      </template>
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              icon: 'clarity:note-edit-line',
              tooltip: '编辑',
              onClick: handleEdit.bind(null, record),
            },
            {
              icon: 'ant-design:delete-outlined',
              color: 'error',
              tooltip: '删除',
              popConfirm: {
                title: '确认删除该条跟进记录吗？',
                placement: 'left',
                confirm: handleDelete.bind(null, record),
              },
            },
          ]"
        />
      </template>
    </BasicTable>
    <FollowModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>
<script lang="ts" setup>
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getFollowRecordPage, deleteFollowRecord } from '/@/api/crm/follow';
  import FollowModal from './FollowModal.vue';
  import { columns, searchFormSchema } from './follow.data';

  const { createMessage } = useMessage();
  const [registerModal, { openModal }] = useModal();

  const [registerTable, { reload }] = useTable({
    title: '跟进记录列表',
    api: getFollowRecordPage,
    columns,
    formConfig: {
      labelWidth: 100,
      schemas: searchFormSchema,
      autoSubmitOnEnter: true,
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    showIndexColumn: false,
    actionColumn: {
      width: 140,
      title: '操作',
      dataIndex: 'action',
      slots: { customRender: 'action' },
    },
    fetchSetting: {
      pageField: 'current',
      sizeField: 'size',
      listField: 'records',
      totalField: 'total',
    },
  });

  function handleCreate() {
    openModal(true, { isUpdate: false });
  }

  function handleEdit(record: Recordable) {
    openModal(true, { record, isUpdate: true });
  }

  async function handleDelete(record: Recordable) {
    await deleteFollowRecord(record.id);
    createMessage.success('删除成功');
    reload();
  }

  function handleSuccess() {
    createMessage.success('操作成功');
    reload();
  }
</script>
