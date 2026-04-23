<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from './leads.data';
  import { saveLeads, updateLeads } from '/@/api/crm/leads';

  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(false);
  const rowId = ref('');

  const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
    labelWidth: 100,
    baseColProps: { span: 12 },
    schemas: formSchema,
    showActionButtonGroup: false,
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    resetFields();
    setModalProps({ confirmLoading: false });
    isUpdate.value = !!data?.isUpdate;

    if (unref(isUpdate)) {
      rowId.value = data.record.id;
      setFieldsValue({ ...data.record });
    }
  });

  const getTitle = computed(() => (!unref(isUpdate) ? '新增线索' : '编辑线索'));

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      if (unref(isUpdate)) {
        await updateLeads({ ...values, id: rowId.value });
      } else {
        await saveLeads(values);
      }
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
