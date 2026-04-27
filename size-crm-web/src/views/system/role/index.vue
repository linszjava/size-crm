<template>
  <div>
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button v-auth="'system:role:add'" type="primary" @click="handleCreate">新增角色</a-button>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableActions(record)" />
      </template>
    </BasicTable>
    <RoleModal @register="registerModal" @success="handleSuccess" />
    <RolePermModal @register="registerPermModal" @success="handleSuccess" />
  </div>
</template>
<script lang="ts">
  import { defineComponent } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { getRolePage, deleteRole } from '/@/api/system/role';
  import { useModal } from '/@/components/Modal';
  import RoleModal from './RoleModal.vue';
  import RolePermModal from './RolePermModal.vue';
  import { columns, searchFormSchema } from './role.data';
  import { usePermission } from '/@/hooks/web/usePermission';

  export default defineComponent({
    name: 'RoleManagement',
    components: { BasicTable, RoleModal, RolePermModal, TableAction },
    setup() {
      const [registerModal, { openModal }] = useModal();
      const [registerPermModal, { openModal: openPermModal }] = useModal();
      const { hasPermission } = usePermission();
      const [registerTable, { reload }] = useTable({
        title: '角色列表',
        api: getRolePage,
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
          width: 260,
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

      function handleAssignPerm(record: Recordable) {
        openPermModal(true, {
          record,
        });
      }

      async function handleDelete(record: Recordable) {
        await deleteRole(record.id);
        reload();
      }

      function getTableActions(record: Recordable) {
        const actions: Recordable[] = [];
        if (hasPermission('system:role:edit')) {
          actions.push({
            icon: 'clarity:note-edit-line',
            label: '编辑',
            onClick: handleEdit.bind(null, record),
          });
          actions.push({
            icon: 'ant-design:safety-certificate-outlined',
            label: '分配权限',
            onClick: handleAssignPerm.bind(null, record),
          });
        }
        if (hasPermission('system:role:delete')) {
          actions.push({
            icon: 'ant-design:delete-outlined',
            label: '删除',
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
        registerPermModal,
        handleCreate,
        handleEdit,
        handleAssignPerm,
        handleDelete,
        getTableActions,
        handleSuccess,
      };
    },
  });
</script>
