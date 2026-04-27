<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    title="分配权限"
    @ok="handleSubmit"
    width="680px"
  >
    <BasicForm @register="registerForm" />
    <div class="role-perm-tree-wrap">
      <BasicTree
        v-model:value="checkedKeys"
        :treeData="menuTreeData"
        checkable
        defaultExpandAll
        :replaceFields="{ title: 'title', key: 'key', children: 'children' }"
      />
    </div>
  </BasicModal>
</template>
<script lang="ts">
  import { defineComponent, ref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, FormSchema, useForm } from '/@/components/Form';
  import { BasicTree } from '/@/components/Tree';
  import { getRole, updateRole } from '/@/api/system/role';
  import { getMenuList } from '/@/api/system/menu';

  function buildMenuTree(flatList: Recordable[] = []): Recordable[] {
    const nodeMap = new Map<string, Recordable>();
    const roots: Recordable[] = [];

    (flatList || []).forEach((item) => {
      const key = String(item.id);
      nodeMap.set(key, {
        title: item.menuName,
        key,
        orderNum: item.orderNum ?? 0,
        children: [],
      });
    });

    (flatList || []).forEach((item) => {
      const current = nodeMap.get(String(item.id));
      if (!current) return;
      const parentId = item.parentId;
      const parentKey = parentId === null || parentId === undefined ? '' : String(parentId);
      if (!parentKey || parentKey === '0' || !nodeMap.has(parentKey)) {
        roots.push(current);
        return;
      }
      const parent = nodeMap.get(parentKey);
      if (!parent) return;
      (parent.children || []).push(current);
    });

    const sortNodes = (nodes: Recordable[]) => {
      nodes.sort((a, b) => Number(a.orderNum || 0) - Number(b.orderNum || 0));
      nodes.forEach((node) => {
        if (node.children?.length) {
          sortNodes(node.children);
        }
      });
    };
    sortNodes(roots);

    return roots;
  }

  const formSchema: FormSchema[] = [
    {
      field: 'id',
      label: 'id',
      component: 'Input',
      show: false,
    },
    {
      field: 'tenantId',
      label: 'tenantId',
      component: 'Input',
      show: false,
    },
    {
      field: 'roleName',
      label: '角色名称',
      component: 'Input',
      dynamicDisabled: true,
    },
    {
      field: 'roleKey',
      label: '权限字符',
      component: 'Input',
      dynamicDisabled: true,
    },
  ];

  export default defineComponent({
    name: 'RolePermModal',
    components: { BasicModal, BasicForm, BasicTree },
    emits: ['success', 'register'],
    setup(_, { emit }) {
      const roleDetail = ref<Recordable>({});
      const checkedKeys = ref<string[]>([]);
      const menuTreeData = ref<Recordable[]>([]);
      const [registerForm, { setFieldsValue, resetFields, validate }] = useForm({
        labelWidth: 100,
        baseColProps: { span: 24 },
        schemas: formSchema,
        showActionButtonGroup: false,
      });

      const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
        resetFields();
        setModalProps({ confirmLoading: false });
        const [detail, menus] = await Promise.all([getRole(data.record.id), getMenuList()]);
        roleDetail.value = detail || {};
        menuTreeData.value = buildMenuTree(menus || []);
        checkedKeys.value = (detail?.menuIds || []).map((id) => String(id));
        setFieldsValue({
          ...detail,
        });
      });

      async function handleSubmit() {
        try {
          await validate();
          setModalProps({ confirmLoading: true });
          await updateRole({
            id: roleDetail.value.id,
            tenantId: roleDetail.value.tenantId,
            roleName: roleDetail.value.roleName,
            roleKey: roleDetail.value.roleKey,
            roleSort: roleDetail.value.roleSort,
            dataScope: roleDetail.value.dataScope,
            status: roleDetail.value.status,
            menuIds: (checkedKeys.value || []).map((id) => Number(id)),
          });
          closeModal();
          emit('success');
        } finally {
          setModalProps({ confirmLoading: false });
        }
      }

      return {
        registerModal,
        registerForm,
        checkedKeys,
        menuTreeData,
        handleSubmit,
      };
    },
  });
</script>
<style lang="less" scoped>
  .role-perm-tree-wrap {
    height: 320px;
    margin-top: 12px;
    padding: 8px 12px;
    overflow: auto;
    border: 1px solid #f0f0f0;
    border-radius: 2px;
  }
</style>
