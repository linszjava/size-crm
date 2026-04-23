<template>
  <div>
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleCreate">新增线索</a-button>
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
              icon: 'ant-design:swap-outlined',
              tooltip: '转化为客户',
              color: 'success',
              ifShow: record.status !== 'CONVERTED',
              popConfirm: {
                title: '确认将该线索转化为客户？',
                placement: 'left',
                confirm: handleConvert.bind(null, record),
              },
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
    <LeadsModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup>
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { getLeadsList, deleteLeads, convertLeadsToCustomer } from '/@/api/crm/leads';
  import { useModal } from '/@/components/Modal';
  import LeadsModal from './LeadsModal.vue';
  import { columns, searchFormSchema } from './leads.data';
  import { useMessage } from '/@/hooks/web/useMessage';

  const { createMessage } = useMessage();
  const [registerModal, { openModal }] = useModal();

  const [registerTable, { reload }] = useTable({
    title: '线索列表',
    api: getLeadsList,
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
      width: 120,
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

  async function handleConvert(record: Recordable) {
    try {
      await convertLeadsToCustomer(record.id);
      createMessage.success('线索已成功转化为客户！');
      reload();
    } catch (error) {
      console.error(error);
    }
  }

  async function handleDelete(record: Recordable) {
    try {
      await deleteLeads(record.id);
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
