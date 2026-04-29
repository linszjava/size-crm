<template>
  <div>
    <BasicTable @register="registerTable">
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              label: '详情',
              icon: 'ant-design:profile-outlined',
              onClick: handleView.bind(null, record),
            },
            {
              label: '通过',
              icon: 'ant-design:check-outlined',
              color: 'success',
              onClick: () => openAuditModal(record, true),
            },
            {
              label: '驳回',
              icon: 'ant-design:close-outlined',
              color: 'error',
              onClick: () => openAuditModal(record, false),
            },
          ]"
        />
      </template>
    </BasicTable>

    <a-modal
      v-model:visible="auditModalVisible"
      :title="auditApproved ? '审批通过' : '审批驳回'"
      :confirm-loading="auditSubmitting"
      ok-text="提交"
      cancel-text="取消"
      destroy-on-close
      @ok="submitAuditFromModal"
    >
      <a-form layout="vertical" class="pt-2">
        <a-form-item label="审批备注">
          <a-textarea
            v-model:value="auditComment"
            :rows="4"
            placeholder="选填：审批意见、补充说明等，将记入流程意见。"
            :maxlength="500"
            show-count
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-drawer
      v-model:visible="detailVisible"
      title="审批详情"
      width="560"
      placement="right"
      :closable="true"
    >
      <a-alert
        v-if="showDataMissingHint"
        type="warning"
        show-icon
        class="mb-3"
        message="未查到完整业务详情"
        description="当前流程的 businessKey 对应业务记录不存在或已被清理，请核对业务数据。"
      />
      <a-descriptions :column="1" bordered size="small" class="mb-4">
        <a-descriptions-item label="任务名称">{{ currentRecord.taskName || '-' }}</a-descriptions-item>
        <a-descriptions-item label="审批类型">{{ bizTypeLabel(currentRecord.bizType) }}</a-descriptions-item>
        <a-descriptions-item label="业务单号">{{ currentRecord.bizNo || '-' }}</a-descriptions-item>
        <a-descriptions-item label="审批标题">{{ currentRecord.bizTitle || '-' }}</a-descriptions-item>
        <a-descriptions-item label="金额">{{ currentRecord.bizAmount ?? '-' }}</a-descriptions-item>
        <a-descriptions-item label="申请人">{{ currentRecord.bizApplicant || '-' }}</a-descriptions-item>
        <a-descriptions-item label="业务摘要">{{ currentRecord.bizSummary || '-' }}</a-descriptions-item>
        <a-descriptions-item label="业务主键">{{ currentRecord.businessKey || '-' }}</a-descriptions-item>
        <a-descriptions-item label="流程实例ID">{{
          currentRecord.processInstanceId || '-'
        }}</a-descriptions-item>
      </a-descriptions>

      <a-card v-if="currentRecord.bizType === 'CONTRACT'" title="合同审批详情" size="small" class="mb-4">
        <a-descriptions :column="1" size="small" bordered>
          <a-descriptions-item label="客户名称">{{ currentRecord.customerName || '-' }}</a-descriptions-item>
          <a-descriptions-item label="客户ID">{{ currentRecord.customerId ?? '-' }}</a-descriptions-item>
          <a-descriptions-item label="关联商机ID">{{
            currentRecord.opportunityId ?? '-'
          }}</a-descriptions-item>
          <a-descriptions-item label="签约日期">{{ currentRecord.signDate || '-' }}</a-descriptions-item>
          <a-descriptions-item label="合同开始日期">{{ currentRecord.startDate || '-' }}</a-descriptions-item>
          <a-descriptions-item label="合同结束日期">{{ currentRecord.endDate || '-' }}</a-descriptions-item>
          <a-descriptions-item label="负责人ID">{{ currentRecord.ownerUserId ?? '-' }}</a-descriptions-item>
        </a-descriptions>
      </a-card>

      <a-card v-if="currentRecord.bizType === 'RECEIVABLE'" title="回款审批详情" size="small">
        <a-descriptions :column="1" size="small" bordered>
          <a-descriptions-item label="客户名称">{{ currentRecord.customerName || '-' }}</a-descriptions-item>
          <a-descriptions-item label="客户ID">{{ currentRecord.customerId ?? '-' }}</a-descriptions-item>
          <a-descriptions-item label="关联合同ID">{{ currentRecord.contractId ?? '-' }}</a-descriptions-item>
          <a-descriptions-item label="回款日期">{{ currentRecord.returnDate || '-' }}</a-descriptions-item>
          <a-descriptions-item label="支付方式">{{ currentRecord.payType || '-' }}</a-descriptions-item>
          <a-descriptions-item label="负责人ID">{{ currentRecord.ownerUserId ?? '-' }}</a-descriptions-item>
        </a-descriptions>
      </a-card>

      <a-card title="完整审批流程" size="small" class="mt-4">
        <a-spin :spinning="progressLoading">
          <a-timeline v-if="progressList.length > 0">
            <a-timeline-item
              v-for="(item, index) in progressList"
              :key="`${item.taskDefinitionKey || index}`"
              :color="item.status === '已审批' ? 'green' : item.status === '待审批' ? 'blue' : 'gray'"
            >
              <div class="mb-1">
                <strong>{{ item.taskName }}</strong>
                <span class="ml-2 text-xs text-gray-500">{{ item.status }}</span>
              </div>
              <div class="text-xs text-gray-500">
                审批人：{{ item.assignee || '待签收/待分配' }}
              </div>
              <div class="text-xs text-gray-500" v-if="item.createTime">
                创建时间：{{ item.createTime }}
              </div>
              <div class="text-xs text-gray-500" v-if="item.endTime">
                完成时间：{{ item.endTime }}
              </div>
              <div class="text-xs text-gray-600" v-if="item.comment">
                审批意见：{{ item.comment }}
              </div>
            </a-timeline-item>
          </a-timeline>
          <a-empty v-else description="暂无流程节点数据" />
        </a-spin>
      </a-card>
    </a-drawer>
  </div>
</template>
<script lang="ts">
  import { defineComponent, ref, computed } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { getMyTasks, completeTask, getProcessProgress } from '/@/api/workflow/task';
  import { columns } from './task.data';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { Drawer, Descriptions, Card, Alert, Timeline, Empty, Spin, Modal, Form, Input } from 'ant-design-vue';
  import { useUserStoreWithOut } from '/@/store/modules/user';

  export default defineComponent({
    name: 'MyTasks',
    components: {
      BasicTable,
      TableAction,
      AModal: Modal,
      AForm: Form,
      AFormItem: Form.Item,
      ATextarea: Input.TextArea,
      ADrawer: Drawer,
      ADescriptions: Descriptions,
      ADescriptionsItem: Descriptions.Item,
      ACard: Card,
      AAlert: Alert,
      ATimeline: Timeline,
      ATimelineItem: Timeline.Item,
      AEmpty: Empty,
      ASpin: Spin,
    },
    setup() {
      const { createMessage } = useMessage();
      const userStore = useUserStoreWithOut();
      const detailVisible = ref(false);
      const currentRecord = ref<Recordable>({});
      const progressLoading = ref(false);
      const progressList = ref<Recordable[]>([]);
      const showDataMissingHint = computed(
        () => currentRecord.value?.bizSummary === '未查询到业务详情',
      );

      const auditModalVisible = ref(false);
      const auditSubmitting = ref(false);
      const auditRecord = ref<Recordable | null>(null);
      const auditApproved = ref(true);
      const auditComment = ref('');

      const [registerTable, { reload }] = useTable({
        title: '我的待办任务',
        api: async () => {
          // 使用真实登录用户与角色，避免同一人“点两次”完成多个节点的错觉
          const userId = String(userStore.getUserInfo?.userId || '10001');
          const roleKeys = (userStore.getRoleList || []).map((r) => String(r));
          return await getMyTasks({ userId, roleKeys });
        },
        columns,
        scroll: { x: 1550 },
        pagination: false,
        striped: false,
        showTableSetting: true,
        bordered: true,
        showIndexColumn: false,
        actionColumn: {
          width: 190,
          title: '操作',
          dataIndex: 'action',
          slots: { customRender: 'action' },
          fixed: undefined,
        },
      });

      function openAuditModal(record: Recordable, approved: boolean) {
        auditRecord.value = record;
        auditApproved.value = approved;
        auditComment.value = '';
        auditModalVisible.value = true;
      }

      async function submitAuditFromModal() {
        const record = auditRecord.value;
        if (!record) return;
        const userId = String(userStore.getUserInfo?.userId || '');
        const roleKeys = (userStore.getRoleList || []).map((r) => String(r));
        if (!userId) {
          createMessage.error('无法获取当前用户，请重新登录');
          return;
        }
        auditSubmitting.value = true;
        try {
          const trimmed = (auditComment.value || '').trim();
          const comment = trimmed || (auditApproved.value ? '同意' : '驳回');
          await completeTask({
            taskId: record.taskId,
            approved: auditApproved.value,
            comment,
            userId,
            roleKeys,
          });
          createMessage.success(auditApproved.value ? '审批通过成功' : '驳回成功');
          auditModalVisible.value = false;
          await reload();
        } catch (e: any) {
          createMessage.error(e?.message || '审批失败，请稍后重试');
          throw e;
        } finally {
          auditSubmitting.value = false;
        }
      }

      async function handleView(record: Recordable) {
        currentRecord.value = record;
        detailVisible.value = true;
        progressList.value = [];
        if (!record.businessKey) return;
        progressLoading.value = true;
        try {
          const data = await getProcessProgress(record.businessKey);
          progressList.value = Array.isArray(data) ? data : [];
        } catch {
          progressList.value = [];
        } finally {
          progressLoading.value = false;
        }
      }

      function bizTypeLabel(type?: string) {
        if (type === 'CONTRACT') return '合同审批';
        if (type === 'RECEIVABLE') return '回款审批';
        return type || '-';
      }

      return {
        registerTable,
        openAuditModal,
        submitAuditFromModal,
        auditModalVisible,
        auditApproved,
        auditComment,
        auditSubmitting,
        handleView,
        detailVisible,
        currentRecord,
        progressLoading,
        progressList,
        bizTypeLabel,
        showDataMissingHint,
      };
    },
  });
</script>
