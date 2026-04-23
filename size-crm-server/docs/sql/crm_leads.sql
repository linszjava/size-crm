-- ====================================================
-- CRM 线索池模块 - 完整数据库脚本
-- ====================================================

USE `size_crm`;

-- ====================================================
-- 1. 线索表 crm_leads
-- ====================================================
DROP TABLE IF EXISTS `crm_leads`;
CREATE TABLE `crm_leads`
(
    `id`               BIGINT(20)  NOT NULL COMMENT '线索ID（雪花算法）',
    `tenant_id`        BIGINT(20)           DEFAULT NULL COMMENT '租户ID',
    `name`             VARCHAR(50) NOT NULL COMMENT '线索姓名',
    `phone`            VARCHAR(20)          DEFAULT NULL COMMENT '联系电话',
    `wechat`           VARCHAR(50)          DEFAULT NULL COMMENT '微信号',
    `email`            VARCHAR(100)         DEFAULT NULL COMMENT '邮箱',
    `company_name`     VARCHAR(100)         DEFAULT NULL COMMENT '所在公司',
    `position`         VARCHAR(50)          DEFAULT NULL COMMENT '职位',
    `industry`         VARCHAR(50)          DEFAULT NULL COMMENT '所属行业',
    `source`           VARCHAR(20)          DEFAULT NULL COMMENT '线索来源(REFERRAL/WEBSITE/EXHIBITION/CALL/AD/OTHER)',
    `status`           VARCHAR(20) NOT NULL DEFAULT 'UNASSIGNED' COMMENT '线索状态(UNASSIGNED/FOLLOWING/CONVERTED/INVALID)',
    `intention`        VARCHAR(10)          DEFAULT NULL COMMENT '意向度(HIGH/MEDIUM/LOW)',
    `owner_user_id`    BIGINT(20)           DEFAULT NULL COMMENT '负责人用户ID',
    `last_follow_time` DATETIME             DEFAULT NULL COMMENT '最近跟进时间',
    `next_follow_time` DATETIME             DEFAULT NULL COMMENT '下次跟进时间',
    `customer_id`      BIGINT(20)           DEFAULT NULL COMMENT '转化后的客户ID',
    `convert_time`     DATETIME             DEFAULT NULL COMMENT '转化时间',
    `remark`           VARCHAR(500)         DEFAULT NULL COMMENT '备注',
    `create_by`        VARCHAR(50)          DEFAULT NULL COMMENT '创建人',
    `create_time`      DATETIME             DEFAULT NULL COMMENT '创建时间',
    `update_by`        VARCHAR(50)          DEFAULT NULL COMMENT '更新人',
    `update_time`      DATETIME             DEFAULT NULL COMMENT '更新时间',
    `del_flag`         TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_source` (`source`),
    KEY `idx_intention` (`intention`),
    KEY `idx_owner_user_id` (`owner_user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'CRM线索池';


-- ====================================================
-- 2. 测试数据（20条，覆盖各种状态/来源/意向度）
-- ====================================================
INSERT INTO `crm_leads` (`id`, `tenant_id`, `name`, `phone`, `wechat`, `email`, `company_name`,
                         `position`, `industry`, `source`, `status`, `intention`,
                         `owner_user_id`, `last_follow_time`, `next_follow_time`,
                         `customer_id`, `convert_time`, `remark`,
                         `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`)
VALUES
-- ① 高意向 跟进中
(1862200000000001, 1, '张伟', '13800138001', 'zhangwei_biz', 'zhangwei@techcorp.com',
 '北京科达信息技术有限公司', '采购总监', '软件/信息服务', 'REFERRAL', 'FOLLOWING', 'HIGH',
 1, '2026-04-20 10:00:00', '2026-04-28 10:00:00', NULL, NULL,
 '朋友介绍，CRM需求明确，预算50万，本周约演示', 'admin', '2026-04-15 09:00:00', 'admin', '2026-04-20 10:00:00', 0),

(1862200000000002, 1, '李娜', '13900139002', 'lina_hr', 'lina@greentech.com.cn',
 '绿能科技（上海）有限公司', 'IT总监', '新能源', 'WEBSITE', 'FOLLOWING', 'HIGH',
 1, '2026-04-21 14:00:00', '2026-04-29 14:00:00', NULL, NULL,
 '官网填表，技术需求详细，决策链清晰', 'admin', '2026-04-16 09:00:00', 'admin', '2026-04-21 14:00:00', 0),

(1862200000000003, 1, '王芳', '18600186003', 'wangfang88', 'wangfang@logistics.cn',
 '顺达物流集团', 'CIO', '物流/供应链', 'REFERRAL', 'FOLLOWING', 'HIGH',
 1, '2026-04-22 11:00:00', '2026-04-30 11:00:00', NULL, NULL,
 '集团级采购，已通过初步评估，等最终汇报', 'admin', '2026-04-18 09:00:00', 'admin', '2026-04-22 11:00:00', 0),

-- ② 中意向 跟进中
(1862200000000004, 1, '陈刚', '13700137004', NULL, 'chengang@mall.com',
 '大悦城商业管理有限公司', '数字化负责人', '商业地产/零售', 'EXHIBITION', 'FOLLOWING', 'MEDIUM',
 1, '2026-04-19 15:00:00', '2026-05-05 15:00:00', NULL, NULL,
 '展会认识，对会员管理模块感兴趣，需配合甲方内部流程', 'admin', '2026-04-10 09:00:00', 'admin', '2026-04-19 15:00:00', 0),

(1862200000000005, 1, '赵雪', '15200152005', 'zhaoxue_pm', NULL,
 '星际互娱（成都）有限公司', '产品总监', '游戏/娱乐', 'AD', 'FOLLOWING', 'MEDIUM',
 1, '2026-04-18 10:00:00', '2026-05-08 10:00:00', NULL, NULL,
 '广告投放来源，对销售管理有需求，预算待确认', 'admin', '2026-04-12 09:00:00', 'admin', '2026-04-18 10:00:00', 0),

(1862200000000006, 1, '刘洋', '13600136006', 'liuyang_boss', 'liuyang@foodchain.com',
 '餐享生活（深圳）餐饮管理集团', '总裁', '餐饮/连锁', 'CALL', 'FOLLOWING', 'MEDIUM',
 1, '2026-04-20 09:00:00', '2026-05-10 09:00:00', NULL, NULL,
 '外呼接通，对加盟商管理系统感兴趣，下周安排视频会议', 'admin', '2026-04-14 09:00:00', 'admin', '2026-04-20 09:00:00', 0),

(1862200000000007, 1, '孙丽', '13500135007', 'sunli_hr', NULL,
 '华康医疗科技集团', 'HR总监', '医疗/健康', 'WEBSITE', 'FOLLOWING', 'MEDIUM',
 1, '2026-04-17 14:00:00', '2026-05-07 14:00:00', NULL, NULL,
 '官网注册体验账号，需要演示，决策周期约2个月', 'admin', '2026-04-13 09:00:00', 'admin', '2026-04-17 14:00:00', 0),

-- ③ 低意向 跟进中
(1862200000000008, 1, '周明', '13400134008', NULL, NULL,
 '个人工作室', '创始人', '广告/设计', 'CALL', 'FOLLOWING', 'LOW',
 1, '2026-04-15 11:00:00', '2026-05-15 11:00:00', NULL, NULL,
 '小微企业，预算极低，考虑到期再跟进', 'admin', '2026-04-08 09:00:00', 'admin', '2026-04-15 11:00:00', 0),

(1862200000000009, 1, '吴静', '18800188009', 'wujing_biz', NULL,
 '云帆教育科技有限公司', '运营总监', '教育/培训', 'AD', 'FOLLOWING', 'LOW',
 1, '2026-04-16 10:00:00', '2026-05-16 10:00:00', NULL, NULL,
 '广告引流，目前资金紧张，来年再做计划', 'admin', '2026-04-09 09:00:00', 'admin', '2026-04-16 10:00:00', 0),

-- ④ 待分配（公海线索）
(1862200000000010, 1, '郑磊', '13300133010', NULL, 'zhenglei@mfg.com.cn',
 '宏远机械制造集团', '副总经理', '机械制造', 'EXHIBITION', 'UNASSIGNED', 'HIGH',
 NULL, NULL, NULL, NULL, NULL,
 '展会名片，高意向，尚未分配销售', 'admin', '2026-04-22 16:00:00', 'admin', '2026-04-22 16:00:00', 0),

(1862200000000011, 1, '徐凤', '15000150011', NULL, NULL,
 '蓝天航空科技有限公司', '采购经理', '航空/军工', 'WEBSITE', 'UNASSIGNED', 'MEDIUM',
 NULL, NULL, NULL, NULL, NULL,
 '官网询盘，等待分配', 'admin', '2026-04-23 09:00:00', 'admin', '2026-04-23 09:00:00', 0),

(1862200000000012, 1, '朱宇', '13200132012', 'zhuyu_work', NULL,
 '晨光文具股份有限公司', '数字化部门负责人', '文化用品/零售', 'REFERRAL', 'UNASSIGNED', 'MEDIUM',
 NULL, NULL, NULL, NULL, NULL,
 '朋友转介绍，未接触，公海待分配', 'admin', '2026-04-23 10:00:00', 'admin', '2026-04-23 10:00:00', 0),

(1862200000000013, 1, '冯勇', '18700187013', NULL, 'fengyong@fintech.com',
 '鑫融金融科技有限公司', 'CTO', '金融科技', 'AD', 'UNASSIGNED', 'HIGH',
 NULL, NULL, NULL, NULL, NULL,
 '广告转化，需求精准，高优先级分配', 'admin', '2026-04-23 11:00:00', 'admin', '2026-04-23 11:00:00', 0),

-- ⑤ 已转化为客户
(1862200000000014, 1, '蒋晨', '13100131014', NULL, 'jiangchen@cloudsoft.com',
 '云尚软件（杭州）有限公司', '总经理', '软件/SaaS', 'REFERRAL', 'CONVERTED', 'HIGH',
 1, '2026-03-10 10:00:00', NULL, 1861100000000016, '2026-03-25 09:00:00',
 '已转化为客户，合同30万元推进中', 'admin', '2026-03-01 09:00:00', 'admin', '2026-03-25 09:00:00', 0),

(1862200000000015, 1, '沈婷', '18600186015', 'shenting88', NULL,
 '天悦连锁超市集团', '信息化总监', '商超/零售', 'CALL', 'CONVERTED', 'HIGH',
 1, '2026-02-20 14:00:00', NULL, 1861100000000019, '2026-03-25 10:00:00',
 '跟进2个月后转化，营销活动管理平台合同30万', 'admin', '2026-02-15 09:00:00', 'admin', '2026-03-25 10:00:00', 0),

-- ⑥ 已无效
(1862200000000016, 1, '卢浩', '13000130016', NULL, NULL,
 '某贸易公司', '老板', '进出口贸易', 'CALL', 'INVALID', 'LOW',
 1, '2026-03-05 10:00:00', NULL, NULL, NULL,
 '多次联系无响应，业务方向不匹配，标记无效', 'admin', '2026-03-01 09:00:00', 'admin', '2026-03-05 10:00:00', 0),

(1862200000000017, 1, '戴丽', '15900159017', NULL, NULL,
 '个人', '自由职业', '其他', 'AD', 'INVALID', 'LOW',
 1, '2026-03-10 11:00:00', NULL, NULL, NULL,
 '广告误点，无采购需求', 'admin', '2026-03-08 09:00:00', 'admin', '2026-03-10 11:00:00', 0),

-- ⑦ 其他行业跟进中
(1862200000000018, 1, '谢天', '18500185018', 'xietian_pm', 'xietian@govit.cn',
 '智慧城市数字化集团', '项目总监', '政府/公共服务', 'REFERRAL', 'FOLLOWING', 'HIGH',
 1, '2026-04-21 09:00:00', '2026-04-28 14:00:00', NULL, NULL,
 '政府智慧城市项目，CRM用于市民服务管理，周期长但体量大', 'admin', '2026-04-17 09:00:00', 'admin', '2026-04-21 09:00:00', 0),

(1862200000000019, 1, '邓欣', '13800138019', NULL, 'dengxin@pharma.com',
 '康佳制药集团', '数字化总监', '医药/生命科学', 'EXHIBITION', 'FOLLOWING', 'MEDIUM',
 1, '2026-04-22 15:00:00', '2026-05-12 15:00:00', NULL, NULL,
 '医药行业合规要求严格，产品需对接合规模块', 'admin', '2026-04-18 09:00:00', 'admin', '2026-04-22 15:00:00', 0),

(1862200000000020, 1, '方浩', '15800158020', 'fanghao_ceo', 'fanghao@agritech.com',
 '丰收数字农业科技有限公司', 'CEO', '农业科技', 'WEBSITE', 'FOLLOWING', 'MEDIUM',
 1, '2026-04-23 10:00:00', '2026-05-20 10:00:00', NULL, NULL,
 '农业行业新客户，对田间管理+客户管理一体化有需求', 'admin', '2026-04-20 09:00:00', 'admin', '2026-04-23 10:00:00', 0);


-- ====================================================
-- 验证查询
-- ====================================================
SELECT status, COUNT(*) AS cnt FROM crm_leads GROUP BY status;
SELECT source, COUNT(*) AS cnt FROM crm_leads GROUP BY source;
SELECT intention, COUNT(*) AS cnt FROM crm_leads GROUP BY intention;
