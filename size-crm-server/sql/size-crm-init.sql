-- SIZE CRM 数据库初始化脚本
-- 包含多租户支持
-- 主键策略: BIGINT (Snowflake)

CREATE DATABASE IF NOT EXISTS `size_crm` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `size_crm`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 系统模块表
-- ----------------------------

-- 多租户表
DROP TABLE IF EXISTS `sys_tenant`;
CREATE TABLE `sys_tenant` (
  `id` bigint NOT NULL COMMENT '租户ID',
  `name` varchar(100) NOT NULL COMMENT '租户名称',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（1正常 0停用）',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户表';

-- 部门表
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL COMMENT '部门ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门ID',
  `ancestors` varchar(500) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint(1) DEFAULT '1' COMMENT '部门状态（1正常 0停用）',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 用户信息表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL COMMENT '用户ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `username` varchar(30) NOT NULL COMMENT '用户账号',
  `nickname` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` tinyint(1) DEFAULT '1' COMMENT '帐号状态（1正常 0停用）',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_username` (`tenant_id`, `username`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- 角色信息表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL COMMENT '角色ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限）',
  `status` tinyint(1) DEFAULT '1' COMMENT '角色状态（1正常 0停用）',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色信息表';

-- 菜单权限表 (通常不需要租户隔离，全局一套菜单)
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL COMMENT '菜单ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `is_frame` tinyint(1) DEFAULT '0' COMMENT '是否为外链（1是 0否）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` tinyint(1) DEFAULT '1' COMMENT '菜单状态（1显示 0隐藏）',
  `status` tinyint(1) DEFAULT '1' COMMENT '菜单状态（1正常 0停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- 用户角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户和角色关联表';

-- 角色菜单关联表
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和菜单关联表';

-- 字典类型表
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `id` bigint NOT NULL COMMENT '字典主键',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（1正常 0停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tenant_dict_type` (`tenant_id`, `dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- 字典数据表
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `id` bigint NOT NULL COMMENT '字典编码',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态（1正常 0停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_dict_type` (`tenant_id`, `dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';

-- ----------------------------
-- 2. CRM 模块表
-- ----------------------------

-- 线索表
DROP TABLE IF EXISTS `crm_leads`;
CREATE TABLE `crm_leads` (
  `id` bigint NOT NULL COMMENT '线索ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `name` varchar(100) NOT NULL COMMENT '线索名称',
  `source` varchar(50) DEFAULT NULL COMMENT '线索来源',
  `company_name` varchar(100) DEFAULT NULL COMMENT '公司名称',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `level` varchar(20) DEFAULT NULL COMMENT '线索级别',
  `industry` varchar(50) DEFAULT NULL COMMENT '所属行业',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `owner_user_id` bigint DEFAULT NULL COMMENT '负责人ID',
  `status` varchar(20) DEFAULT 'UNFOLLOW' COMMENT '线索状态（UNFOLLOW未跟进, FOLLOWING跟进中, TRANSFORMED已转化, DEAD无效）',
  `customer_id` bigint DEFAULT NULL COMMENT '转化后的客户ID',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_owner` (`tenant_id`, `owner_user_id`),
  KEY `idx_tenant_status` (`tenant_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM线索表';

-- 客户表
DROP TABLE IF EXISTS `crm_customer`;
CREATE TABLE `crm_customer` (
  `id` bigint NOT NULL COMMENT '客户ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `name` varchar(100) NOT NULL COMMENT '客户名称',
  `level` varchar(20) DEFAULT NULL COMMENT '客户级别（如 VIP、普通等，字典）',
  `source` varchar(50) DEFAULT NULL COMMENT '客户来源（字典）',
  `industry` varchar(50) DEFAULT NULL COMMENT '所属行业（字典）',
  `scale` varchar(20) DEFAULT NULL COMMENT '人员规模（字典）',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话',
  `website` varchar(100) DEFAULT NULL COMMENT '网址',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区县',
  `address` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `deal_status` tinyint(1) DEFAULT '0' COMMENT '成交状态（0未成交 1已成交）',
  `owner_user_id` bigint DEFAULT NULL COMMENT '负责人ID（为空表示在公海）',
  `roster_time` datetime DEFAULT NULL COMMENT '分配时间或捞取时间',
  `next_follow_time` datetime DEFAULT NULL COMMENT '下次跟进时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_owner` (`tenant_id`, `owner_user_id`),
  KEY `idx_tenant_name` (`tenant_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM客户表';

-- 联系人表
DROP TABLE IF EXISTS `crm_contact`;
CREATE TABLE `crm_contact` (
  `id` bigint NOT NULL COMMENT '联系人ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `customer_id` bigint NOT NULL COMMENT '所属客户ID',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `post` varchar(50) DEFAULT NULL COMMENT '职务',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `telephone` varchar(20) DEFAULT NULL COMMENT '座机',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `wechat` varchar(50) DEFAULT NULL COMMENT '微信号',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `is_primary` tinyint(1) DEFAULT '0' COMMENT '是否关键决策人（1是 0否）',
  `next_follow_time` datetime DEFAULT NULL COMMENT '下次跟进时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_customer` (`tenant_id`, `customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM联系人表';

-- 商机表
DROP TABLE IF EXISTS `crm_opportunity`;
CREATE TABLE `crm_opportunity` (
  `id` bigint NOT NULL COMMENT '商机ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `name` varchar(100) NOT NULL COMMENT '商机名称',
  `expected_amount` decimal(15,2) DEFAULT NULL COMMENT '预计销售金额',
  `expected_date` date DEFAULT NULL COMMENT '预计签单日期',
  `sales_stage` varchar(20) DEFAULT NULL COMMENT '销售阶段（发现,评估,报价,谈判,赢单,输单等）',
  `win_rate` decimal(5,2) DEFAULT NULL COMMENT '赢单率(%)',
  `owner_user_id` bigint NOT NULL COMMENT '负责人ID',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_customer` (`tenant_id`, `customer_id`),
  KEY `idx_tenant_owner` (`tenant_id`, `owner_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM商机表';

-- 合同表
DROP TABLE IF EXISTS `crm_contract`;
CREATE TABLE `crm_contract` (
  `id` bigint NOT NULL COMMENT '合同ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `opportunity_id` bigint DEFAULT NULL COMMENT '商机ID',
  `name` varchar(100) NOT NULL COMMENT '合同名称',
  `contract_no` varchar(50) NOT NULL COMMENT '合同编号',
  `total_amount` decimal(15,2) NOT NULL COMMENT '合同总金额',
  `sign_date` date DEFAULT NULL COMMENT '签约日期',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `owner_user_id` bigint NOT NULL COMMENT '负责人ID',
  `process_instance_id` varchar(64) DEFAULT NULL COMMENT '工作流实例ID（Flowable）',
  `audit_status` varchar(20) DEFAULT '0' COMMENT '审核状态（0待审核 1审核中 2审核通过 3审核拒绝）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tenant_contract_no` (`tenant_id`, `contract_no`),
  KEY `idx_tenant_customer` (`tenant_id`, `customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM合同表';

-- 回款单表
DROP TABLE IF EXISTS `crm_receivable`;
CREATE TABLE `crm_receivable` (
  `id` bigint NOT NULL COMMENT '回款ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `receivable_no` varchar(50) NOT NULL COMMENT '回款编号',
  `amount` decimal(15,2) NOT NULL COMMENT '回款金额',
  `return_date` date NOT NULL COMMENT '回款日期',
  `pay_type` varchar(20) DEFAULT NULL COMMENT '支付方式',
  `owner_user_id` bigint NOT NULL COMMENT '负责人ID',
  `process_instance_id` varchar(64) DEFAULT NULL COMMENT '工作流实例ID（Flowable）',
  `audit_status` varchar(20) DEFAULT '0' COMMENT '审核状态',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_contract` (`tenant_id`, `contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM回款单表';

-- 跟进记录表
DROP TABLE IF EXISTS `crm_follow_record`;
CREATE TABLE `crm_follow_record` (
  `id` bigint NOT NULL COMMENT '记录ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `biz_type` varchar(20) NOT NULL COMMENT '业务类型(LEADS线索, CUSTOMER客户, CONTACT联系人, OPPORTUNITY商机)',
  `biz_id` bigint NOT NULL COMMENT '业务ID',
  `follow_type` varchar(20) DEFAULT NULL COMMENT '跟进方式(电话,微信,拜访等)',
  `content` text NOT NULL COMMENT '跟进内容',
  `next_time` datetime DEFAULT NULL COMMENT '下次联系时间',
  `record_user_id` bigint NOT NULL COMMENT '记录人ID',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_biz` (`tenant_id`, `biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM跟进记录表';

SET FOREIGN_KEY_CHECKS = 1;
