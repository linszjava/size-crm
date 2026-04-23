import { BasicPageParams, BasicFetchResult } from '/@/api/model/baseModel';

/** 线索状态枚举 */
export const LEADS_STATUS_MAP: Record<string, { label: string; color: string }> = {
  UNASSIGNED: { label: '待分配', color: 'default' },
  FOLLOWING:  { label: '跟进中', color: 'processing' },
  CONVERTED:  { label: '已转化', color: 'success' },
  INVALID:    { label: '已无效', color: 'error' },
};

/** 线索来源枚举 */
export const LEADS_SOURCE_MAP: Record<string, string> = {
  REFERRAL:   '转介绍',
  WEBSITE:    '官网询盘',
  EXHIBITION: '展会',
  CALL:       '电话外呼',
  AD:         '广告投放',
  OTHER:      '其他',
};

/** 意向度枚举 */
export const LEADS_INTENTION_MAP: Record<string, { label: string; color: string }> = {
  HIGH:   { label: '高意向', color: 'red' },
  MEDIUM: { label: '中意向', color: 'orange' },
  LOW:    { label: '低意向', color: 'default' },
};

export interface LeadsParams extends BasicPageParams {
  name?: string;
  phone?: string;
  status?: string;
  source?: string;
  intention?: string;
}

export interface LeadsModel {
  id: string;
  tenantId: string;
  name: string;
  phone: string;
  wechat?: string;
  email?: string;
  companyName?: string;
  position?: string;
  industry?: string;
  source?: string;
  status: string;
  intention?: string;
  ownerUserId?: string;
  lastFollowTime?: string;
  nextFollowTime?: string;
  customerId?: string;
  convertTime?: string;
  remark?: string;
  createTime?: string;
  updateTime?: string;
}

export type LeadsPageListGetResultModel = BasicFetchResult<LeadsModel>;
