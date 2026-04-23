<template>
  <div>
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleCreate">新增回款</a-button>
      </template>
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              icon: 'clarity:note-edit-line',
              onClick: handleEdit.bind(null, record),
              ifShow: record.auditStatus === 'DRAFT' || record.auditStatus === 'REJECTED'
            },
            {
              icon: 'ant-design:safety-certificate-outlined',
              color: 'success',
              tooltip: record.auditStatus === 'REJECTED' ? '重新发起审批' : '发起审批',
              onClick: handleSubmitAudit.bind(null, record),
              ifShow: record.auditStatus === 'DRAFT' || record.auditStatus === 'REJECTED'
            },
            {
              icon: 'ant-design:delete-outlined',
              color: 'error',
              popConfirm: {
                title: '是否确认删除',
                placement: 'left',
                confirm: handleDelete.bind(null, record),
              },
              ifShow: record.auditStatus === 'DRAFT' || record.auditStatus === 'REJECTED'
            },
          ]"
        />
      </template>
    </BasicTable>
    <ReceivableModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>
<script lang="ts">
  import { defineComponent } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { getReceivablePage, deleteReceivable, submitReceivableAudit } from '/@/api/crm/receivable';
  import { useModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import ReceivableModal from './ReceivableModal.vue';
  import { columns, searchFormSchema } from './receivable.data';

  export default defineComponent({
    name: 'ReceivableManagement',
    components: { BasicTable, ReceivableModal, TableAction },
    setup() {
      const { createMessage } = useMessage();
      const [registerModal, { openModal }] = useModal();
      const [registerTable, { reload }] = useTable({
        title: '回款列表',
        api: getReceivablePage,
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
          width: 160,
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
        await deleteReceivable(record.id);
        reload();
      }

      async function handleSubmitAudit(record: Recordable) {
        try {
          await submitReceivableAudit(record.id);
          createMessage.success('已成功发起审批，当前状态流转为审批中');
          reload();
        } catch (error) {
          // 异常由拦截器处理
        }
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
        handleSubmitAudit,
        handleSuccess,
      };
    },
  });
</script>
