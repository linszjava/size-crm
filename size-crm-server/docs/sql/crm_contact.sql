-- ====================================================
-- CRM 联系人模块 - 完整数据库脚本
-- ====================================================

USE `size_crm`;

-- ====================================================
-- 1. 联系人表 crm_contact
-- ====================================================
DROP TABLE IF EXISTS `crm_contact`;
CREATE TABLE `crm_contact`
(
    `id`               BIGINT(20)  NOT NULL COMMENT '联系人ID（雪花算法）',
    `tenant_id`        BIGINT(20)           DEFAULT NULL COMMENT '租户ID',
    `customer_id`      BIGINT(20)           DEFAULT NULL COMMENT '所属客户ID',
    `name`             VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    `post`             VARCHAR(50)          DEFAULT NULL COMMENT '职务/职位',
    `phone`            VARCHAR(20)          DEFAULT NULL COMMENT '手机号',
    `telephone`        VARCHAR(30)          DEFAULT NULL COMMENT '座机',
    `email`            VARCHAR(100)         DEFAULT NULL COMMENT '邮箱',
    `wechat`           VARCHAR(50)          DEFAULT NULL COMMENT '微信号',
    `address`          VARCHAR(255)         DEFAULT NULL COMMENT '地址',
    `is_primary`       TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '是否关键决策人(0-否 1-是)',
    `next_follow_time` DATETIME             DEFAULT NULL COMMENT '下次跟进时间',
    `remark`           VARCHAR(500)         DEFAULT NULL COMMENT '备注',
    `create_by`        VARCHAR(50)          DEFAULT NULL COMMENT '创建人',
    `create_time`      DATETIME             DEFAULT NULL COMMENT '创建时间',
    `update_by`        VARCHAR(50)          DEFAULT NULL COMMENT '更新人',
    `update_time`      DATETIME             DEFAULT NULL COMMENT '更新时间',
    `del_flag`         TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_customer_id` (`customer_id`),
    KEY `idx_is_primary` (`is_primary`),
    KEY `idx_phone` (`phone`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'CRM联系人';


-- ====================================================
-- 2. 测试数据（与 crm_customer 真实关联）
-- ====================================================
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


-- ====================================================
-- 验证查询
-- ====================================================
SELECT COUNT(*) AS total FROM crm_contact;
-- 关键决策人数量
SELECT is_primary, COUNT(*) AS cnt FROM crm_contact GROUP BY is_primary;
-- 每个客户的联系人数量
SELECT c.name AS customer_name, COUNT(ct.id) AS contact_count
FROM crm_contact ct
LEFT JOIN crm_customer c ON ct.customer_id = c.id
GROUP BY ct.customer_id, c.name
ORDER BY contact_count DESC;
