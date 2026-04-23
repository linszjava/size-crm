<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>
<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form';
  import { formSchema } from './follow.data';
  import { saveFollowRecord, updateFollowRecord } from '/@/api/crm/follow';
  import { getUserList } from '/@/api/system/user';
  import { getCustomerList } from '/@/api/crm/customer';
  import { getLeadsList } from '/@/api/crm/leads';
  import { getContactList } from '/@/api/crm/contact';
  import { getOpportunityPage } from '/@/api/crm/opportunity';
  import { getContractPage } from '/@/api/crm/contract';
  import { getReceivablePage } from '/@/api/crm/receivable';

  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(true);
  const rowId = ref('');

  const [registerForm, { resetFields, setFieldsValue, validate, updateSchema }] = useForm({
    labelWidth: 110,
    baseColProps: { span: 24 },
    schemas: formSchema,
    showActionButtonGroup: false,
  });

  const userOptions = ref<{ label: string; value: string | number }[]>([]);
  const bizOptions = ref<{ label: string; value: string | number }[]>([]);

  function buildUserOptions(list: Recordable[]) {
    return (list || []).map((item) => ({
      label: item.nickName || item.username || `用户${item.id}`,
      value: item.id,
    }));
  }

  function buildBizOptions(list: Recordable[], bizType: string) {
    return (list || []).map((item) => {
      let label = item.name || item.title || `记录${item.id}`;
      if (bizType === 'CONTRACT') {
        label = `${item.contractNo || '-'} / ${item.name || item.id}`;
      }
      if (bizType === 'RECEIVABLE') {
        label = `${item.receivableNo || '-'} / ${item.amount || 0}`;
      }
      if (bizType === 'LEADS') {
        label = `${item.name || item.id} / ${item.phone || '-'}`;
      }
      if (bizType === 'CONTACT') {
        label = `${item.name || item.id} / ${item.phone || '-'}`;
      }
      return { label, value: item.id };
    });
  }

  async function loadUserOptions() {
    try {
      const list = (await getUserList()) as Recordable[];
      userOptions.value = buildUserOptions(list || []);
    } catch {
      userOptions.value = [];
    }
    await updateSchema({
      field: 'recordUserId',
      componentProps: {
        options: userOptions.value,
        showSearch: true,
        optionFilterProp: 'label',
      },
    });
  }

  async function loadBizOptionsByType(bizType?: string) {
    if (!bizType) {
      bizOptions.value = [];
      await updateSchema({
        field: 'bizId',
        componentProps: {
          options: [],
          showSearch: true,
          optionFilterProp: 'label',
          placeholder: '请先选择业务类型',
        },
      });
      return;
    }
    let records: Recordable[] = [];
    const pageParams = { current: 1, size: 200 };
    if (bizType === 'CUSTOMER') {
      const res = (await getCustomerList(pageParams)) as Recordable;
      records = res?.records || [];
    } else if (bizType === 'LEADS') {
      const res = (await getLeadsList(pageParams)) as Recordable;
      records = res?.records || [];
    } else if (bizType === 'CONTACT') {
      const res = (await getContactList(pageParams)) as Recordable;
      records = res?.records || [];
    } else if (bizType === 'OPPORTUNITY') {
      const res = (await getOpportunityPage(pageParams)) as Recordable;
      records = res?.records || [];
    } else if (bizType === 'CONTRACT') {
      const res = (await getContractPage(pageParams)) as Recordable;
      records = res?.records || [];
    } else if (bizType === 'RECEIVABLE') {
      const res = (await getReceivablePage(pageParams)) as Recordable;
      records = res?.records || [];
    }
    bizOptions.value = buildBizOptions(records, bizType);
    await updateSchema({
      field: 'bizId',
      componentProps: {
        options: bizOptions.value,
        showSearch: true,
        optionFilterProp: 'label',
        placeholder: bizOptions.value.length ? '请选择业务记录' : '暂无可选业务记录',
      },
    });
  }

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    resetFields();
    setModalProps({ confirmLoading: false });
    isUpdate.value = !!data?.isUpdate;
    await updateSchema({
      field: 'bizType',
      componentProps: {
        onChange: async (val: string) => {
          await loadBizOptionsByType(val);
          setFieldsValue({ bizId: undefined });
        },
      },
    });
    await loadUserOptions();

    if (unref(isUpdate)) {
      rowId.value = data.record.id;
      await loadBizOptionsByType(data.record.bizType);
      setFieldsValue({
        ...data.record,
      });
    } else {
      await loadBizOptionsByType();
    }
  });

  const getTitle = computed(() => (!unref(isUpdate) ? '新增跟进记录' : '编辑跟进记录'));

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      if (unref(isUpdate)) {
        await updateFollowRecord({ ...values, id: rowId.value });
      } else {
        await saveFollowRecord(values);
      }
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

