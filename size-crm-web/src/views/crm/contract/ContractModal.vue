<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit">
    <div class="mb-3" v-if="!isUpdate">
      <a-button type="default" @click="handleSaveDraft" :loading="draftLoading">保存草稿</a-button>
    </div>
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>
<script lang="ts">
  import { defineComponent, ref, computed, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from './contract.data';
  import { saveContract, saveContractDraft, updateContract } from '/@/api/crm/contract';
  import { useMessage } from '/@/hooks/web/useMessage';

  export default defineComponent({
    name: 'ContractModal',
    components: { BasicModal, BasicForm },
    emits: ['success', 'register'],
    setup(_, { emit }) {
      const isUpdate = ref(true);
      const rowId = ref('');
      const draftLoading = ref(false);
      const { createMessage } = useMessage();

      const [registerForm, { setFieldsValue, resetFields, validate, getFieldsValue }] = useForm({
        labelWidth: 100,
        baseColProps: { span: 24 },
        schemas: formSchema,
        showActionButtonGroup: false,
      });

      const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
        resetFields();
        setModalProps({ confirmLoading: false });
        isUpdate.value = !!data?.isUpdate;

        if (unref(isUpdate)) {
          rowId.value = data.record.id;
          setFieldsValue({
            ...data.record,
          });
        }
      });

      const getTitle = computed(() => (!unref(isUpdate) ? '新增合同' : '编辑合同'));

      async function handleSaveDraft() {
        try {
          draftLoading.value = true;
          const values = getFieldsValue();
          await saveContractDraft(values);
          createMessage.success('草稿已保存');
          closeModal();
          emit('success');
        } finally {
          draftLoading.value = false;
        }
      }

      async function handleSubmit() {
        try {
          const values = await validate();
          setModalProps({ confirmLoading: true });
          if (unref(isUpdate)) {
             await updateContract(values);
          } else {
             await saveContract(values);
          }
          closeModal();
          emit('success');
        } finally {
          setModalProps({ confirmLoading: false });
        }
      }

      return { registerModal, registerForm, getTitle, handleSubmit, handleSaveDraft, isUpdate, draftLoading };
    },
  });
</script>
