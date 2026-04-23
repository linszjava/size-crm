<template>
  <div>
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleCreate">新增合同</a-button>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableActions(record)" />
      </template>
    </BasicTable>
    <ContractModal @register="registerModal" @success="handleSuccess" />
    <ProgressModal @register="registerProgressModal" />
  </div>
</template>
<script lang="ts">
  import { defineComponent } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { getContractPage, deleteContract, submitContractAudit } from '/@/api/crm/contract';
  import { useModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import ContractModal from './ContractModal.vue';
  import ProgressModal from './ProgressModal.vue';
  import { columns, searchFormSchema } from './contract.data';
  import type { ActionItem } from '/@/components/Table';

  export default defineComponent({
    name: 'ContractManagement',
    components: { BasicTable, ContractModal, ProgressModal, TableAction },
    setup() {
      const { createMessage } = useMessage();
      const [registerModal, { openModal }] = useModal();
      const [registerProgressModal, { openModal: openProgressModal }] = useModal();
      const [registerTable, { reload }] = useTable({
        title: '合同列表',
        api: getContractPage,
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
          width: 200,
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

      function handleView(record: Recordable) {
        openModal(true, { record, isUpdate: true });
      }

      function handleProgress(record: Recordable) {
        openProgressModal(true, { businessKey: `contract:${record.id}` });
      }

      async function handleDelete(record: Recordable) {
        await deleteContract(record.id);
        reload();
      }

      async function handleSubmitAudit(record: Recordable) {
        try {
          await submitContractAudit(record.id);
          createMessage.success('审批发起成功，状态已流转为审批中');
          reload();
        } catch (e) {
          // 由全局拦截器处理
        }
      }

      function handleSuccess() {
        reload();
      }

      function getTableActions(record: Recordable): ActionItem[] {
        const status = record.auditStatus;
        const canEdit = status === 'DRAFT' || status === 'REJECTED';
        const canAudit = status === 'DRAFT' || status === 'REJECTED';
        const canView = status === 'AUDITING' || status === 'APPROVED';

        const actions: ActionItem[] = [];

        if (canEdit) {
          actions.push({
            icon: 'clarity:note-edit-line',
            tooltip: '编辑',
            onClick: handleEdit.bind(null, record),
          });
        }

        if (canAudit) {
          actions.push({
            icon: 'ant-design:safety-certificate-outlined',
            color: 'success',
            tooltip: status === 'REJECTED' ? '重新发起审批' : '发起审批',
            onClick: handleSubmitAudit.bind(null, record),
          });
        }

        if (canView) {
          actions.push({
            icon: 'ant-design:eye-outlined',
            tooltip: '查看',
            onClick: handleView.bind(null, record),
          });
          actions.push({
            icon: 'ant-design:history-outlined',
            tooltip: '查看进度',
            color: 'warning',
            onClick: handleProgress.bind(null, record),
          });
        }

        if (canEdit) {
          actions.push({
            icon: 'ant-design:delete-outlined',
            color: 'error',
            tooltip: '删除',
            popConfirm: {
              title: '确认删除该合同吗？',
              placement: 'left',
              confirm: handleDelete.bind(null, record),
            },
          });
        }

        return actions;
      }

      return {
        registerTable,
        registerModal,
        registerProgressModal,
        handleCreate,
        handleSuccess,
        getTableActions,
      };
    },
  });
</script>
