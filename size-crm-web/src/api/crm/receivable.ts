import { defHttp } from '/@/utils/http/axios';

const API_BASE = '/crm/receivable';

export const getReceivablePage = (params?: any) =>
  defHttp.get({ url: `${API_BASE}/page`, params });

export const getReceivable = (id: string) =>
  defHttp.get({ url: `${API_BASE}/${id}` });

export const saveReceivable = (data: any) =>
  defHttp.post({ url: API_BASE, data });

export const updateReceivable = (data: any) =>
  defHttp.put({ url: API_BASE, data });

export const deleteReceivable = (ids: string) =>
  defHttp.delete({ url: `${API_BASE}/${ids}` });

// 提交审批接口
export const submitReceivableAudit = (id: string) =>
  defHttp.post({ url: `${API_BASE}/submitAudit/${id}` });
