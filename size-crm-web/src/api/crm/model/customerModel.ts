import { BasicPageParams, BasicFetchResult } from '/@/api/model/baseModel';

export interface CustomerParams extends BasicPageParams {
  name?: string;
  phone?: string;
  level?: string;
}

export interface CustomerModel {
  id: string;
  tenantId: string;
  name: string;
  level: string;
  source: string;
  industry: string;
  scale: string;
  phone: string;
  website: string;
  province: string;
  city: string;
  district: string;
  address: string;
  dealStatus: number;
  ownerUserId: string;
  rosterTime: string;
  nextFollowTime: string;
  remark: string;
  createTime: string;
  updateTime: string;
}

export type CustomerPageListGetResultModel = BasicFetchResult<CustomerModel>;
