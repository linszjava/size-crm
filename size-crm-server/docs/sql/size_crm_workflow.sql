-- =============================================================================
-- size-crm-workflow 微服务 | 数据库 size_crm_workflow
-- =============================================================================
-- 本文件仅负责「建库」。Camunda 引擎表（ACT_* 等）由应用首次启动时
-- camunda.bpm.database.schema-update=true 自动创建，勿在此手写引擎 DDL。
-- 与前身 Flowable 无关；历史 flowable_tmp 等脚本已删除。

CREATE DATABASE IF NOT EXISTS `size_crm_workflow`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
