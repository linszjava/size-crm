CREATE TABLE `crm_opportunity` (
  `id` bigint NOT NULL COMMENT '商机ID',
  `tenant_id` bigint DEFAULT NULL COMMENT '租户ID',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `name` varchar(255) NOT NULL COMMENT '商机名称',
  `expected_amount` decimal(15,2) DEFAULT NULL COMMENT '预计销售金额',
  `expected_date` date DEFAULT NULL COMMENT '预计签单日期',
  `sales_stage` varchar(50) DEFAULT NULL COMMENT '销售阶段',
  `win_rate` decimal(5,2) DEFAULT NULL COMMENT '赢单率(%)',
  `owner_user_id` bigint DEFAULT NULL COMMENT '负责人ID',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` int DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商机表';
