<template>
  <div>
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleCreate">新增联系人</a-button>
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
              tooltip: '删除',
              color: 'error',
              popConfirm: {
                title: '是否确认删除',
                placement: 'left',
                confirm: handleDelete.bind(null, record),
              },
            },
          ]"
        />
      </template>
    </BasicTable>
    <ContactModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup>
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { getContactList, deleteContact } from '/@/api/crm/contact';
  import { useModal } from '/@/components/Modal';
  import ContactModal from './ContactModal.vue';
  import { columns, searchFormSchema } from './contact.data';
  import { useMessage } from '/@/hooks/web/useMessage';

  const { createMessage } = useMessage();
  const [registerModal, { openModal }] = useModal();

  const [registerTable, { reload }] = useTable({
    title: '联系人列表',
    api: getContactList,
    columns,
    formConfig: {
      labelWidth: 80,
      schemas: searchFormSchema,
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    showIndexColumn: false,
    actionColumn: {
      width: 100,
      title: '操作',
      dataIndex: 'action',
      slots: { customRender: 'action' },
      fixed: undefined,
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
    try {
      await deleteContact(record.id);
      createMessage.success('删除成功');
      reload();
    } catch (error) {
      console.error(error);
    }
  }

  function handleSuccess() {
    createMessage.success('操作成功');
    reload();
  }
</script>

<style scoped></style>
