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
              popConfirm: {
                title: '是否确认审批通过？',
                placement: 'left',
                confirm: handleComplete.bind(null, record, true),
              },
            },
            {
              label: '驳回',
              icon: 'ant-design:close-outlined',
              color: 'error',
              popConfirm: {
                title: '是否确认驳回？',
                placement: 'left',
                confirm: handleComplete.bind(null, record, false),
              },
            },
          ]"
        />
      </template>
    </BasicTable>
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
  import { Drawer, Descriptions, Card, Alert, Timeline, Empty, Spin } from 'ant-design-vue';
  import { useUserStoreWithOut } from '/@/store/modules/user';

  export default defineComponent({
    name: 'MyTasks',
    components: {
      BasicTable,
      TableAction,
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

      async function handleComplete(record: Recordable, approved: boolean) {
        try {
          await completeTask({
            taskId: record.taskId,
            approved: approved,
            comment: approved ? '同意' : '驳回',
          });
          createMessage.success(approved ? '审批通过成功' : '驳回成功');
          reload();
        } catch (e: any) {
          createMessage.error(e?.message || '审批失败，请稍后重试');
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
        handleComplete,
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
