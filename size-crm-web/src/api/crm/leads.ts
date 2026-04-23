import { defHttp } from '/@/utils/http/axios';
import { LeadsParams, LeadsModel, LeadsPageListGetResultModel } from './model/leadsModel';

enum Api {
  LeadsPage = '/crm/leads/page',
  Leads     = '/crm/leads',
  Convert   = '/crm/leads/convert',
}

/** 分页查询线索列表 */
export const getLeadsList = (params?: LeadsParams) =>
  defHttp.get<LeadsPageListGetResultModel>({ url: Api.LeadsPage, params });

/** 获取线索详情 */
export const getLeadsInfo = (id: string) =>
  defHttp.get<LeadsModel>({ url: `${Api.Leads}/${id}` });

/** 新增线索 */
export const saveLeads = (data: Partial<LeadsModel>) =>
  defHttp.post<boolean>({ url: Api.Leads, data });

/** 修改线索 */
export const updateLeads = (data: Partial<LeadsModel>) =>
  defHttp.put<boolean>({ url: Api.Leads, data });

/** 删除线索 */
export const deleteLeads = (id: string) =>
  defHttp.delete<boolean>({ url: `${Api.Leads}/${id}` });

/** 一键转化为客户 */
export const convertLeadsToCustomer = (id: string) =>
  defHttp.post<string>({ url: `${Api.Convert}/${id}` });
