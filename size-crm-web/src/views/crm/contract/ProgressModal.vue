<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="审批进度" :footer="null">
    <div class="p-4">
      <a-timeline>
        <a-timeline-item
          v-for="(item, index) in progressList"
          :key="index"
          :color="item.status === '已审批' ? 'green' : 'gray'"
        >
          <div class="mb-2">
            <strong>{{ item.taskName }}</strong>
            <span
              class="ml-4 text-sm"
              :class="item.status === '已审批' ? 'text-green-600' : 'text-gray-500'"
            >
              {{ item.status }}
            </span>
          </div>
          <div v-if="item.assignee" class="mb-1 text-sm">
            审批人: {{ item.assignee }}
          </div>
          <div v-else class="mb-1 text-sm text-gray-400">
            审批人: 等待签收/处理
          </div>
          <div v-if="item.createTime" class="mb-1 text-sm text-gray-500">
            创建时间: {{ formatToDateTime(item.createTime) }}
          </div>
          <div v-if="item.endTime" class="mb-1 text-sm text-gray-500">
            完成时间: {{ formatToDateTime(item.endTime) }}
          </div>
          <div v-if="item.comment" class="mt-2 p-2 bg-gray-100 rounded text-sm">
            审批意见: {{ item.comment }}
          </div>
        </a-timeline-item>
      </a-timeline>
      <div v-if="progressList.length === 0" class="text-center text-gray-500">
        暂无审批记录
      </div>
    </div>
  </BasicModal>
</template>

<script lang="ts">
  import { defineComponent, ref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { getProcessProgress } from '/@/api/workflow/task';
  import { formatToDateTime } from '/@/utils/dateUtil';
  import { Timeline } from 'ant-design-vue';

  export default defineComponent({
    name: 'ProgressModal',
    components: {
      BasicModal,
      [Timeline.name]: Timeline,
      [Timeline.Item.name]: Timeline.Item,
    },
    setup() {
      const progressList = ref<any[]>([]);

      const [registerModal] = useModalInner(async (data) => {
        progressList.value = [];
        if (data && data.businessKey) {
          const res = await getProcessProgress(data.businessKey);
          progressList.value = res || [];
        }
      });

      return {
        registerModal,
        progressList,
        formatToDateTime,
      };
    },
  });
</script>
