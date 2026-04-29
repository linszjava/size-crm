-- =============================================================================
-- size-crm-biz 微服务 | 数据库 size_crm
-- 说明：仅 crm_* 等业务表。与 size_crm_system、size_crm_workflow 物理分离。
-- =============================================================================

CREATE DATABASE IF NOT EXISTS `size_crm` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `size_crm`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- CRM 模块表
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
  `process_instance_id` varchar(64) DEFAULT NULL COMMENT '工作流实例ID（Camunda）',
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
  `process_instance_id` varchar(64) DEFAULT NULL COMMENT '工作流实例ID（Camunda）',
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

-- ---- 可选：客户、联系人样例（来自历史 docs 脚本，已与 init 中表结构对齐）----
INSERT INTO `crm_customer` (`id`, `tenant_id`, `name`, `level`, `source`, `industry`, `scale`, `phone`,
                            `website`, `province`, `city`, `district`, `address`, `deal_status`,
                            `owner_user_id`, `roster_time`, `next_follow_time`, `remark`,
                            `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`)
VALUES
-- ① VIP 已成交
(1861100000000001, 1, '深圳华为技术有限公司', 'VIP', 'REFERRAL', '通信/互联网', '10000人以上',
 '0755-28780808', 'https://www.huawei.com', '广东省', '深圳市', '龙岗区', '华为基地坂田B区',
 1, 1, '2026-01-10 09:00:00', NULL, '战略合作伙伴，年度采购合同已签署', 'admin', '2026-01-10 09:00:00', 'admin', '2026-04-10 10:00:00', 0),

(1861100000000002, 1, '阿里巴巴（中国）有限公司', 'VIP', 'WEBSITE', '电子商务/零售', '10000人以上',
 '0571-85022088', 'https://www.alibaba.com', '浙江省', '杭州市', '余杭区', '阿里巴巴西溪园区',
 1, 1, '2026-01-15 10:00:00', NULL, '电商SaaS定制项目，合同金额120万', 'admin', '2026-01-15 10:00:00', 'admin', '2026-04-15 10:00:00', 0),

(1861100000000003, 1, '腾讯科技（深圳）有限公司', 'VIP', 'REFERRAL', '互联网/软件', '10000人以上',
 '0755-86013388', 'https://www.tencent.com', '广东省', '深圳市', '南山区', '腾讯滨海大厦',
 1, 1, '2026-02-01 09:00:00', NULL, '企业微信集成项目，二期合同洽谈中', 'admin', '2026-02-01 09:00:00', 'admin', '2026-04-20 09:00:00', 0),

-- ② 普通客户 已成交
(1861100000000004, 1, '北京字节跳动科技有限公司', 'NORMAL', 'EXHIBITION', '互联网/媒体', '1000-5000人',
 '010-65102288', 'https://www.bytedance.com', '北京市', '北京市', '海淀区', '字节跳动总部大厦',
 1, 1, '2026-02-10 14:00:00', NULL, '数据分析平台采购，合同50万元', 'admin', '2026-02-10 14:00:00', 'admin', '2026-04-10 14:00:00', 0),

(1861100000000005, 1, '美团点评（北京）信息技术有限公司', 'NORMAL', 'CALL', '本地生活/O2O', '5000-10000人',
 '010-57289999', 'https://www.meituan.com', '北京市', '北京市', '朝阳区', '美团龙泽园区',
 1, 1, '2026-02-20 10:00:00', NULL, 'CRM系统定制开发，合同68万元', 'admin', '2026-02-20 10:00:00', 'admin', '2026-03-20 10:00:00', 0),

(1861100000000006, 1, '苏宁易购集团股份有限公司', 'NORMAL', 'WEBSITE', '零售/电商', '5000-10000人',
 '025-86009999', 'https://www.suning.com', '江苏省', '南京市', '玄武区', '苏宁总部徐庄软件园',
 1, 1, '2026-03-01 09:30:00', NULL, '供应链管理系统升级项目', 'admin', '2026-03-01 09:30:00', 'admin', '2026-04-01 09:30:00', 0),

-- ③ 潜力客户 未成交 跟进中
(1861100000000007, 1, '小米科技有限责任公司', 'POTENTIAL', 'REFERRAL', '消费电子/IoT', '1000-5000人',
 '010-60606060', 'https://www.mi.com', '北京市', '北京市', '海淀区', '小米科技园二期',
 0, 1, '2026-03-05 11:00:00', '2026-05-10 10:00:00', '已演示产品Demo，等待采购部审批', 'admin', '2026-03-05 11:00:00', 'admin', '2026-04-23 11:00:00', 0),

(1861100000000008, 1, '网易（杭州）网络有限公司', 'POTENTIAL', 'EXHIBITION', '互联网/游戏', '1000-5000人',
 '0571-89853163', 'https://www.netease.com', '浙江省', '杭州市', '滨江区', '网易滨江总部',
 0, 1, '2026-03-10 14:00:00', '2026-05-15 14:00:00', '技术对接完成，商务条款协商中', 'admin', '2026-03-10 14:00:00', 'admin', '2026-04-23 14:00:00', 0),

(1861100000000009, 1, '京东集团股份有限公司', 'VIP', 'REFERRAL', '电子商务/物流', '10000人以上',
 '010-89106666', 'https://www.jd.com', '北京市', '北京市', '亦庄经开区', '京东总部大厦',
 0, 1, '2026-03-15 09:00:00', '2026-05-20 09:00:00', '大客户专项跟进，采购预算800万', 'admin', '2026-03-15 09:00:00', 'admin', '2026-04-23 09:00:00', 0),

(1861100000000010, 1, '拼多多（上海）网络科技有限公司', 'NORMAL', 'WEBSITE', '社交电商', '1000-5000人',
 '021-55302888', 'https://www.pinduoduo.com', '上海市', '上海市', '长宁区', '拼多多研发中心',
 0, 1, '2026-03-20 10:00:00', '2026-05-05 10:00:00', '营销自动化系统需求调研完成', 'admin', '2026-03-20 10:00:00', 'admin', '2026-04-22 10:00:00', 0),

-- ④ 制造业客户
(1861100000000011, 1, '比亚迪股份有限公司', 'VIP', 'REFERRAL', '新能源汽车/制造', '10000人以上',
 '0755-89888888', 'https://www.byd.com', '广东省', '深圳市', '坪山区', '比亚迪股份有限公司总部',
 1, 1, '2026-01-20 09:00:00', NULL, '智能工厂数字化管理项目，合同280万', 'admin', '2026-01-20 09:00:00', 'admin', '2026-04-20 09:00:00', 0),

(1861100000000012, 1, '宁德时代新能源科技股份有限公司', 'VIP', 'REFERRAL', '新能源/电池制造', '10000人以上',
 '0593-8899998', 'https://www.catl.com', '福建省', '宁德市', '蕉城区', '宁德时代总部基地',
 0, 1, '2026-04-01 10:00:00', '2026-05-25 10:00:00', '供应链协同平台项目立项审批中', 'admin', '2026-04-01 10:00:00', 'admin', '2026-04-23 10:00:00', 0),

(1861100000000013, 1, '格力电器（珠海）股份有限公司', 'NORMAL', 'CALL', '家电制造', '5000-10000人',
 '0756-8522888', 'https://www.gree.com.cn', '广东省', '珠海市', '香洲区', '格力工业园',
 1, 1, '2026-02-15 09:00:00', NULL, '售后服务管理系统，合同45万元', 'admin', '2026-02-15 09:00:00', 'admin', '2026-03-15 09:00:00', 0),

-- ⑤ 金融行业
(1861100000000014, 1, '招商银行股份有限公司', 'VIP', 'REFERRAL', '金融/银行', '10000人以上',
 '0755-83389999', 'https://www.cmbchina.com', '广东省', '深圳市', '福田区', '招商银行大厦',
 0, 1, '2026-04-05 14:00:00', '2026-06-01 14:00:00', '银行客户关系管理系统采购，需通过银监会合规评审', 'admin', '2026-04-05 14:00:00', 'admin', '2026-04-23 14:00:00', 0),

(1861100000000015, 1, '蚂蚁科技集团股份有限公司', 'VIP', 'REFERRAL', '金融科技', '5000-10000人',
 '0571-26888888', 'https://www.antgroup.com', '浙江省', '杭州市', '滨江区', '蚂蚁Z空间',
 1, 1, '2026-03-01 09:00:00', NULL, '支付风控系统集成合同，年服务费150万', 'admin', '2026-03-01 09:00:00', 'admin', '2026-04-01 09:00:00', 0),

-- ⑥ 中小企业客户
(1861100000000016, 1, '广州市云端科技有限公司', 'NORMAL', 'WEBSITE', '软件/SaaS', '100-500人',
 '020-88889999', NULL, '广东省', '广州市', '天河区', '天河科技园C栋1501',
 0, 1, '2026-04-10 10:00:00', '2026-05-01 10:00:00', '初创企业，预算有限，考虑标准版产品', 'admin', '2026-04-10 10:00:00', 'admin', '2026-04-23 10:00:00', 0),

(1861100000000017, 1, '成都智链数字科技有限公司', 'NORMAL', 'EXHIBITION', '企业服务/SaaS', '50-200人',
 '028-65336688', NULL, '四川省', '成都市', '高新区', '天府三街688号数字大厦',
 0, 1, '2026-04-12 14:00:00', '2026-04-30 14:00:00', '展会接触，对CRM产品感兴趣，已发方案书', 'admin', '2026-04-12 14:00:00', 'admin', '2026-04-23 14:00:00', 0),

(1861100000000018, 1, '杭州数海信息技术有限公司', 'POTENTIAL', 'OTHER', '大数据/AI', '200-500人',
 '0571-56787788', 'https://www.shuhai.tech', '浙江省', '杭州市', '西湖区', '西湖科技园B区',
 0, 1, '2026-04-15 11:00:00', '2026-05-08 11:00:00', '通过朋友圈广告主动询盘，技术需求明确', 'admin', '2026-04-15 11:00:00', 'admin', '2026-04-23 11:00:00', 0),

(1861100000000019, 1, '上海赋能网络科技股份有限公司', 'NORMAL', 'CALL', '营销科技', '500-1000人',
 '021-51882288', 'https://www.funeng.com', '上海市', '上海市', '静安区', '静安嘉里中心写字楼',
 1, 1, '2026-03-25 10:00:00', NULL, '营销活动管理平台定制，合同30万，交付中', 'admin', '2026-03-25 10:00:00', 'admin', '2026-04-20 10:00:00', 0),

(1861100000000020, 1, '武汉天朗数字文旅集团有限公司', 'POTENTIAL', 'REFERRAL', '文旅/景区', '200-500人',
 '027-87778888', NULL, '湖北省', '武汉市', '江岸区', '武汉天地商务区T5栋',
 0, 1, '2026-04-20 09:00:00', '2026-05-15 09:00:00', '景区数字化管理需求，政府采购通道，周期较长', 'admin', '2026-04-20 09:00:00', 'admin', '2026-04-23 09:00:00', 0);

INSERT INTO `crm_contact` (`id`, `tenant_id`, `customer_id`, `name`, `post`, `phone`, `telephone`,
                           `email`, `wechat`, `address`, `is_primary`, `next_follow_time`, `remark`,
                           `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`)
VALUES
-- 华为（1861100000000001）
(1863300000000001, 1, 1861100000000001, '李国栋', '采购总监', '13800000001', '0755-28780001',
 'lgd@huawei.com', 'lgd_hw', NULL, 1, '2026-05-10 10:00:00',
 '合同主要对接人，决策链顶端', 'admin', '2026-01-10 09:00:00', 'admin', '2026-04-10 09:00:00', 0),
(1863300000000002, 1, 1861100000000001, '王晓敏', 'IT主管', '13800000002', NULL,
 'wxm@huawei.com', 'wangxm_hw', NULL, 0, NULL,
 '技术对接人，负责需求整理', 'admin', '2026-01-10 09:30:00', 'admin', '2026-04-10 09:30:00', 0),

-- 阿里巴巴（1861100000000002）
(1863300000000003, 1, 1861100000000002, '张云帆', '电商技术总监', '13900000003', '0571-85022001',
 'zyf@alibaba.com', 'zyf_ali', NULL, 1, '2026-05-15 14:00:00',
 '主导选型，技术评估负责人', 'admin', '2026-01-15 10:00:00', 'admin', '2026-04-15 10:00:00', 0),
(1863300000000004, 1, 1861100000000002, '周晨希', '产品经理', '15000000004', NULL,
 'zcx@alibaba.com', NULL, NULL, 0, NULL,
 '产品需求对接', 'admin', '2026-01-15 10:30:00', 'admin', '2026-04-15 10:30:00', 0),

-- 腾讯（1861100000000003）
(1863300000000005, 1, 1861100000000003, '陈浩然', 'CTO', '13700000005', '0755-86013001',
 'chr@tencent.com', 'chr_tx', NULL, 1, '2026-05-20 09:00:00',
 '技术决策者，企业微信项目主负责人', 'admin', '2026-02-01 09:00:00', 'admin', '2026-04-20 09:00:00', 0),

-- 字节跳动（1861100000000004）
(1863300000000006, 1, 1861100000000004, '刘思远', '数据分析负责人', '18600000006', NULL,
 'lsy@bytedance.com', 'lsy_by', NULL, 1, NULL,
 '数据平台采购决策人', 'admin', '2026-02-10 14:00:00', 'admin', '2026-04-10 14:00:00', 0),

-- 比亚迪（1861100000000011）
(1863300000000007, 1, 1861100000000011, '孙明远', '智能制造总监', '13600000007', '0755-89888001',
 'smy@byd.com', 'smy_byd', NULL, 1, '2026-05-25 10:00:00',
 '智能工厂项目核心负责人', 'admin', '2026-01-20 09:00:00', 'admin', '2026-04-20 09:00:00', 0),
(1863300000000008, 1, 1861100000000011, '余佳琪', '信息化部门主管', '18500000008', NULL,
 'yjq@byd.com', NULL, NULL, 0, NULL,
 '日常系统对接人', 'admin', '2026-01-20 09:30:00', 'admin', '2026-04-20 09:30:00', 0),

-- 蚂蚁集团（1861100000000015）
(1863300000000009, 1, 1861100000000015, '胡雨桐', '风控技术负责人', '13500000009', '0571-26888001',
 'hyt@antgroup.com', 'hyt_ant', NULL, 1, NULL,
 '支付风控系统对接核心人', 'admin', '2026-03-01 09:00:00', 'admin', '2026-04-01 09:00:00', 0),

-- 小米（1861100000000007）
(1863300000000010, 1, 1861100000000007, '冯晨亮', 'IoT产品总监', '13400000010', '010-60606001',
 'fcl@mi.com', 'fcl_mi', NULL, 1, '2026-05-10 10:00:00',
 '等待采购部审批，是主要推动者', 'admin', '2026-03-05 11:00:00', 'admin', '2026-04-23 11:00:00', 0),

-- 宁德时代（1861100000000012）
(1863300000000011, 1, 1861100000000012, '蒋志远', '供应链信息化总监', '13300000011', '0593-8899001',
 'jzy@catl.com', 'jzy_catl', NULL, 1, '2026-05-25 10:00:00',
 '供应链协同平台立项推动人', 'admin', '2026-04-01 10:00:00', 'admin', '2026-04-23 10:00:00', 0),

-- 格力（1861100000000013）
(1863300000000012, 1, 1861100000000013, '林海燕', '售后服务部总监', '15200000012', '0756-8522001',
 'lhy@gree.com.cn', NULL, NULL, 1, NULL,
 '售后系统合同已签，交付中', 'admin', '2026-02-15 09:00:00', 'admin', '2026-03-15 09:00:00', 0),

-- 美团（1861100000000005）
(1863300000000013, 1, 1861100000000005, '赵阳', 'CRM项目负责人', '18700000013', NULL,
 'zhaoyang@meituan.com', 'zhaoyang_mt', NULL, 1, NULL,
 'CRM定制开发项目对接人，合同执行中', 'admin', '2026-02-20 10:00:00', 'admin', '2026-03-20 10:00:00', 0),

-- 招商银行（1861100000000014）
(1863300000000014, 1, 1861100000000014, '徐文博', '数字化部门副总', '13200000014', '0755-83389001',
 'xwb@cmbchina.com', 'xwb_cmb', NULL, 1, '2026-06-01 14:00:00',
 '银行合规审评期间的主要联系人', 'admin', '2026-04-05 14:00:00', 'admin', '2026-04-23 14:00:00', 0),

-- 拼多多（1861100000000010）
(1863300000000015, 1, 1861100000000010, '吴浩然', '营销技术总监', '13100000015', NULL,
 'whr@pinduoduo.com', NULL, NULL, 1, '2026-05-05 10:00:00',
 '营销自动化方案主导人', 'admin', '2026-03-20 10:00:00', 'admin', '2026-04-22 10:00:00', 0);

-- ---- 可选：商机/合同/回款/跟进全链路样例 ----
-- 为了重复执行，先清空业务数据
DELETE FROM `crm_follow_record`;
DELETE FROM `crm_receivable`;
DELETE FROM `crm_contract`;
DELETE FROM `crm_opportunity`;

-- ====================================================
-- 1) 商机数据
-- ====================================================
INSERT INTO `crm_opportunity` (`id`, `tenant_id`, `customer_id`, `name`, `expected_amount`, `expected_date`,
                               `sales_stage`, `win_rate`, `owner_user_id`, `remark`,
                               `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`)
VALUES
(1864400000000001, 1, 1861100000000001, '华为 CRM 二期扩容', 2600000.00, '2026-06-30', 'NEGOTIATION', 80.00, 1, '预算已批复，等待采购排期', 'admin', '2026-04-01 09:00:00', 'admin', '2026-04-20 09:00:00', 0),
(1864400000000002, 1, 1861100000000002, '阿里多事业部统一客户中台', 4200000.00, '2026-07-20', 'PROPOSAL', 70.00, 1, '方案评审中', 'admin', '2026-04-02 10:00:00', 'admin', '2026-04-22 10:00:00', 0),
(1864400000000003, 1, 1861100000000007, '小米 IoT 渠道 CRM 项目', 950000.00, '2026-05-31', 'QUALIFIED', 55.00, 1, '需求明确，待商务条款', 'admin', '2026-04-03 11:00:00', 'admin', '2026-04-23 11:00:00', 0),
(1864400000000004, 1, 1861100000000011, '比亚迪 经销商管理平台', 3000000.00, '2026-07-05', 'NEGOTIATION', 75.00, 1, '正在法务审阅', 'admin', '2026-04-04 14:00:00', 'admin', '2026-04-23 14:00:00', 0),
(1864400000000005, 1, 1861100000000014, '招行 零售客户经营中台', 1800000.00, '2026-08-10', 'QUALIFIED', 45.00, 1, '合规审核周期较长', 'admin', '2026-04-05 15:00:00', 'admin', '2026-04-23 15:00:00', 0),
(1864400000000006, 1, 1861100000000019, '赋能网络营销活动管理升级', 380000.00, '2026-05-25', 'CLOSED_WON', 100.00, 1, '已成交，进入交付', 'admin', '2026-03-20 10:00:00', 'admin', '2026-04-10 09:00:00', 0);

-- ====================================================
-- 2) 合同数据
-- ====================================================
INSERT INTO `crm_contract` (`id`, `tenant_id`, `customer_id`, `opportunity_id`, `name`, `contract_no`, `total_amount`,
                            `sign_date`, `start_date`, `end_date`, `owner_user_id`, `process_instance_id`, `audit_status`,
                            `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`)
VALUES
(1865500000000001, 1, 1861100000000001, 1864400000000001, '华为 CRM 二期正式合同', 'HT-2026-001', 1200000.00, '2026-04-10', '2026-04-15', '2027-04-14', 1, 'PROC_CONTRACT_001', 'AUDITING', '审批中，待总监签批', 'admin', '2026-04-10 09:00:00', 'admin', '2026-04-23 16:55:49', 0),
(1865500000000002, 1, 1861100000000011, 1864400000000004, '比亚迪年度一期补充协议', 'HT-2026-002', 300000.00, '2026-05-15', '2026-05-20', '2026-12-31', 1, 'PROC_CONTRACT_002', 'APPROVED', '已审批通过', 'admin', '2026-04-12 09:30:00', 'admin', '2026-04-23 16:55:49', 0),
(1865500000000003, 1, 1861100000000002, 1864400000000002, '阿里客户中台实施合同', 'HT-2026-003', 2800000.00, '2026-06-01', '2026-06-05', '2027-06-04', 1, NULL, 'DRAFT', '待发起审批', 'admin', '2026-04-18 10:00:00', 'admin', '2026-04-23 10:00:00', 0),
(1865500000000004, 1, 1861100000000007, 1864400000000003, '小米 IoT 渠道管理合同', 'HT-2026-004', 860000.00, '2026-05-28', '2026-06-01', '2027-05-31', 1, 'PROC_CONTRACT_004', 'REJECTED', '商务条款需调整后重提', 'admin', '2026-04-19 11:00:00', 'admin', '2026-04-23 11:00:00', 0);

-- ====================================================
-- 3) 回款数据
-- ====================================================
INSERT INTO `crm_receivable` (`id`, `tenant_id`, `customer_id`, `contract_id`, `receivable_no`, `amount`, `return_date`,
                              `pay_type`, `owner_user_id`, `process_instance_id`, `audit_status`, `remark`,
                              `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`)
VALUES
(1866600000000001, 1, 1861100000000001, 1865500000000001, 'SK-2026-001', 400000.00, '2026-04-20', 'BANK', 1, 'PROC_RECEIVABLE_001', 'AUDITING', '首期款审批中', 'admin', '2026-04-20 10:00:00', 'admin', '2026-04-23 16:00:00', 0),
(1866600000000002, 1, 1861100000000011, 1865500000000002, 'SK-2026-002', 300000.00, '2026-05-25', 'BANK', 1, 'PROC_RECEIVABLE_002', 'APPROVED', '已到账', 'admin', '2026-04-21 10:30:00', 'admin', '2026-04-23 16:00:00', 0),
(1866600000000003, 1, 1861100000000002, 1865500000000003, 'SK-2026-003', 600000.00, '2026-06-20', 'ALIPAY', 1, NULL, 'DRAFT', '待提交审批', 'admin', '2026-04-22 09:30:00', 'admin', '2026-04-23 09:30:00', 0),
(1866600000000004, 1, 1861100000000007, 1865500000000004, 'SK-2026-004', 180000.00, '2026-06-30', 'WECHAT', 1, 'PROC_RECEIVABLE_004', 'REJECTED', '金额与合同节点不一致，已驳回', 'admin', '2026-04-22 11:30:00', 'admin', '2026-04-23 11:30:00', 0);

-- ====================================================
-- 4) 跟进记录数据（覆盖客户/线索/联系人/商机/合同/回款）
-- ====================================================
INSERT INTO `crm_follow_record` (`id`, `tenant_id`, `biz_type`, `biz_id`, `follow_type`, `content`, `next_time`,
                                 `record_user_id`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`)
VALUES
(1867700000000001, 1, 'CUSTOMER', 1861100000000001, 'PHONE', '电话确认华为二期排期，客户希望5月中旬启动。', '2026-04-28 10:00:00', 1, 0, 'admin', '2026-04-23 09:10:00', 'admin', '2026-04-23 09:10:00'),
(1867700000000002, 1, 'CUSTOMER', 1861100000000002, 'MEETING', '完成阿里方案评审会，需补充多组织权限设计。', '2026-04-29 15:00:00', 1, 0, 'admin', '2026-04-23 09:25:00', 'admin', '2026-04-23 09:25:00'),
(1867700000000003, 1, 'LEADS', 1862200000000001, 'WECHAT', '线索张伟反馈预算已确认，安排下周POC。', '2026-04-30 11:00:00', 1, 0, 'admin', '2026-04-23 09:40:00', 'admin', '2026-04-23 09:40:00'),
(1867700000000004, 1, 'LEADS', 1862200000000010, 'PHONE', '公海线索郑磊首轮沟通，需求清晰，建议尽快分配。', '2026-04-27 14:00:00', 1, 0, 'admin', '2026-04-23 09:55:00', 'admin', '2026-04-23 09:55:00'),
(1867700000000005, 1, 'CONTACT', 1863300000000001, 'VISIT', '拜访华为采购总监，确认法务条款可接受。', '2026-05-06 10:30:00', 1, 0, 'admin', '2026-04-23 10:10:00', 'admin', '2026-04-23 10:10:00'),
(1867700000000006, 1, 'CONTACT', 1863300000000014, 'EMAIL', '向招行联系人发送合规材料清单，等待反馈。', '2026-05-08 16:00:00', 1, 0, 'admin', '2026-04-23 10:25:00', 'admin', '2026-04-23 10:25:00'),
(1867700000000007, 1, 'OPPORTUNITY', 1864400000000001, 'MEETING', '商机进入谈判阶段，预计6月底签单。', '2026-05-05 09:30:00', 1, 0, 'admin', '2026-04-23 10:40:00', 'admin', '2026-04-23 10:40:00'),
(1867700000000008, 1, 'OPPORTUNITY', 1864400000000005, 'PHONE', '金融行业项目需补齐等保与审计报告。', '2026-05-12 11:30:00', 1, 0, 'admin', '2026-04-23 10:55:00', 'admin', '2026-04-23 10:55:00'),
(1867700000000009, 1, 'CONTRACT', 1865500000000001, 'WECHAT', '合同HT-2026-001已进入审批中，提醒审批节点负责人处理。', '2026-04-25 09:00:00', 1, 0, 'admin', '2026-04-23 11:10:00', 'admin', '2026-04-23 11:10:00'),
(1867700000000010, 1, 'CONTRACT', 1865500000000004, 'MEETING', '被驳回合同已开复盘会，准备修订价格条款后重提。', '2026-04-30 14:30:00', 1, 0, 'admin', '2026-04-23 11:25:00', 'admin', '2026-04-23 11:25:00'),
(1867700000000011, 1, 'RECEIVABLE', 1866600000000001, 'PHONE', '首期款审批催办，财务已补齐凭证。', '2026-04-26 10:00:00', 1, 0, 'admin', '2026-04-23 11:40:00', 'admin', '2026-04-23 11:40:00'),
(1867700000000012, 1, 'RECEIVABLE', 1866600000000004, 'EMAIL', '驳回回款单已发送修订版，等待重新提交。', '2026-05-06 15:30:00', 1, 0, 'admin', '2026-04-23 11:55:00', 'admin', '2026-04-23 11:55:00');

-- 快速校验
SELECT COUNT(*) AS opportunity_total FROM crm_opportunity;
SELECT COUNT(*) AS contract_total FROM crm_contract;
SELECT COUNT(*) AS receivable_total FROM crm_receivable;
SELECT COUNT(*) AS follow_total FROM crm_follow_record;
