SQL 按微服务归档（均在 docs/sql/）：
  size_crm_system.sql   → size-crm-system，库名 size_crm_system
  size_crm.sql          → size-crm-biz，库名 size_crm
  size_crm_workflow.sql → size-crm-workflow，库名 size_crm_workflow（仅建库，表由 Camunda 自动建）
  size_crm_workflow_reset_camunda.sql → 清空 ACT_/FLW_ 表后重启工作流服务，用于修复引擎表版本不一致（开发环境）
