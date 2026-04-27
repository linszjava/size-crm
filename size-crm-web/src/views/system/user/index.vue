<template>
  <div>
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button v-auth="'system:user:add'" type="primary" @click="handleCreate">新增用户</a-button>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableActions(record)" />
      </template>
    </BasicTable>
    <UserModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>
<script lang="ts">
  import { defineComponent } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { getUserPage, deleteUser } from '/@/api/system/user';
  import { useModal } from '/@/components/Modal';
  import UserModal from './UserModal.vue';
  import { columns, searchFormSchema } from './user.data';
  import { usePermission } from '/@/hooks/web/usePermission';

  export default defineComponent({
    name: 'UserManagement',
    components: { BasicTable, UserModal, TableAction },
    setup() {
      const [registerModal, { openModal }] = useModal();
      const { hasPermission } = usePermission();
      const [registerTable, { reload }] = useTable({
        title: '用户列表',
        api: getUserPage,
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
          width: 120,
          title: '操作',
          dataIndex: 'action',
          slots: { customRender: 'action' },
          fixed: 'right',
        },
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
        await deleteUser(record.id);
        reload();
      }

      function getTableActions(record: Recordable) {
        const actions: Recordable[] = [];
        if (hasPermission('system:user:edit')) {
          actions.push({
            icon: 'clarity:note-edit-line',
            onClick: handleEdit.bind(null, record),
          });
        }
        if (hasPermission('system:user:delete')) {
          actions.push({
            icon: 'ant-design:delete-outlined',
            color: 'error',
            popConfirm: {
              title: '是否确认删除',
              placement: 'left',
              confirm: handleDelete.bind(null, record),
            },
          });
        }
        return actions;
      }

      function handleSuccess() {
        reload();
      }

      return {
        registerTable,
        registerModal,
        handleCreate,
        handleEdit,
        handleDelete,
        getTableActions,
        handleSuccess,
      };
    },
  });
</script>
