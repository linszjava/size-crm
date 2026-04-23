<template>
  <div>
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleCreate"> 新增客户 </a-button>
      </template>
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              icon: 'clarity:note-edit-line',
              onClick: handleEdit.bind(null, record),
            },
            {
              icon: 'ant-design:delete-outlined',
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
    <CustomerModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>
<script lang="ts" setup>
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { getCustomerList, deleteCustomer } from '/@/api/crm/customer';
  import { useModal } from '/@/components/Modal';
  import CustomerModal from './CustomerModal.vue';
  import { columns, searchFormSchema } from './customer.data';
  import { useMessage } from '/@/hooks/web/useMessage';

  const { createMessage } = useMessage();
  const [registerModal, { openModal }] = useModal();
  
  const [registerTable, { reload }] = useTable({
    title: '客户列表',
    api: getCustomerList,
    columns,
    formConfig: {
      labelWidth: 120,
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
    // 将后端的 data 字段解构出来给表格
    fetchSetting: {
      pageField: 'current',
      sizeField: 'size',
      listField: 'records',
      totalField: 'total',
    },
  });

  function handleCreate() {
    openModal(true, {
      isUpdate: false,
    });
  }

  function handleEdit(record: Recordable) {
    openModal(true, {
      record,
      isUpdate: true,
    });
  }

  async function handleDelete(record: Recordable) {
    try {
      await deleteCustomer(record.id);
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
