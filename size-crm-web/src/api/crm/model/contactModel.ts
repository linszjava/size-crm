import { BasicPageParams, BasicFetchResult } from '/@/api/model/baseModel';

export interface ContactParams extends BasicPageParams {
  name?: string;
  phone?: string;
  customerId?: string;
}

export interface ContactModel {
  id: string;
  tenantId?: string;
  customerId?: string;
  name: string;
  post?: string;
  phone?: string;
  telephone?: string;
  email?: string;
  wechat?: string;
  address?: string;
  isPrimary?: number;
  nextFollowTime?: string;
  remark?: string;
  createTime?: string;
  updateTime?: string;
}

export type ContactPageListGetResultModel = BasicFetchResult<ContactModel>;
