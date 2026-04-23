<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    title="流程图"
    :footer="null"
    width="980px"
    :canFullscreen="true"
  >
    <div class="h-[70vh] border rounded bg-white" ref="canvasRef"></div>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, nextTick, onBeforeUnmount } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { getProcessDefinitionXml } from '/@/api/workflow/definition';
  import { useMessage } from '/@/hooks/web/useMessage';

  const { createMessage } = useMessage();
  const canvasRef = ref<HTMLElement | null>(null);
  let viewer: any = null;

  async function renderXml(xml: string) {
    await nextTick();
    if (!canvasRef.value) return;
    const { default: BpmnViewer } = await import('bpmn-js/lib/NavigatedViewer');
    if (viewer) {
      try {
        viewer.destroy();
      } catch {}
      viewer = null;
    }
    viewer = new BpmnViewer({
      container: canvasRef.value,
    });
    try {
      await viewer.importXML(xml);
      const canvas = viewer.get('canvas');
      canvas.zoom('fit-viewport');
    } catch (e: any) {
      createMessage.error(e?.message || '渲染流程图失败');
    }
  }

  const [registerModal] = useModalInner(async (data) => {
    if (!data?.definitionId) return;
    try {
      const xml = await getProcessDefinitionXml(String(data.definitionId));
      await renderXml(String(xml || ''));
    } catch (e: any) {
      createMessage.error(e?.message || '加载流程XML失败');
    }
  });

  onBeforeUnmount(() => {
    if (viewer) {
      try {
        viewer.destroy();
      } catch {}
    }
  });
</script>

