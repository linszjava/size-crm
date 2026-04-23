-- ====================================================
-- CRM 客户管理模块 - 完整数据库脚本
-- 数据库：size_crm
-- 创建时间：2026-04-23
-- ====================================================

-- 建库（可选，如已存在则跳过）
CREATE DATABASE IF NOT EXISTS `size_crm`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `size_crm`;

-- ====================================================
-- 1. 客户主表 crm_customer
-- ====================================================
DROP TABLE IF EXISTS `crm_customer`;
CREATE TABLE `crm_customer`
(
    `id`              BIGINT(20)   NOT NULL COMMENT '客户ID（雪花算法）',
    `tenant_id`       BIGINT(20)            DEFAULT NULL COMMENT '租户ID',
    `name`            VARCHAR(100) NOT NULL COMMENT '客户名称',
    `level`           VARCHAR(20)           DEFAULT NULL COMMENT '客户级别（VIP / NORMAL / POTENTIAL）',
    `source`          VARCHAR(20)           DEFAULT NULL COMMENT '客户来源（REFERRAL/WEBSITE/EXHIBITION/CALL/OTHER）',
    `industry`        VARCHAR(50)           DEFAULT NULL COMMENT '所属行业',
    `scale`           VARCHAR(20)           DEFAULT NULL COMMENT '人员规模',
    `phone`           VARCHAR(20)           DEFAULT NULL COMMENT '联系电话',
    `website`         VARCHAR(255)          DEFAULT NULL COMMENT '官方网站',
    `province`        VARCHAR(30)           DEFAULT NULL COMMENT '省份',
    `city`            VARCHAR(30)           DEFAULT NULL COMMENT '城市',
    `district`        VARCHAR(30)           DEFAULT NULL COMMENT '区县',
    `address`         VARCHAR(255)          DEFAULT NULL COMMENT '详细地址',
    `deal_status`     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '成交状态（0-未成交 1-已成交）',
    `owner_user_id`   BIGINT(20)            DEFAULT NULL COMMENT '负责人用户ID',
    `roster_time`     DATETIME              DEFAULT NULL COMMENT '分配/捞取时间',
    `next_follow_time` DATETIME              DEFAULT NULL COMMENT '下次跟进时间',
    `remark`          VARCHAR(500)          DEFAULT NULL COMMENT '备注',
    `create_by`       VARCHAR(50)           DEFAULT NULL COMMENT '创建人',
    `create_time`     DATETIME              DEFAULT NULL COMMENT '创建时间',
    `update_by`       VARCHAR(50)           DEFAULT NULL COMMENT '更新人',
    `update_time`     DATETIME              DEFAULT NULL COMMENT '更新时间',
    `del_flag`        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除（0-正常 1-已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_owner_user_id` (`owner_user_id`),
    KEY `idx_level` (`level`),
    KEY `idx_deal_status` (`deal_status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'CRM客户主表';


-- ====================================================
-- 2. 测试数据（20条真实感样本）
-- ====================================================
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


-- ====================================================
-- 验证查询
-- ====================================================
-- 查看总数
SELECT COUNT(*) AS total FROM crm_customer;

-- 按级别统计
SELECT level, COUNT(*) AS cnt, SUM(deal_status) AS deal_count
FROM crm_customer
GROUP BY level
ORDER BY cnt DESC;

-- 按成交状态统计
SELECT
    CASE deal_status WHEN 1 THEN '已成交' ELSE '未成交' END AS status,
    COUNT(*) AS cnt
FROM crm_customer
GROUP BY deal_status;
