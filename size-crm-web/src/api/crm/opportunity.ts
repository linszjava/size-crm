import { defHttp } from '/@/utils/http/axios';

const API_BASE = '/crm/opportunity';

export const getOpportunityPage = (params?: any) =>
  defHttp.get({ url: `${API_BASE}/page`, params });

export const getOpportunity = (id: string) =>
  defHttp.get({ url: `${API_BASE}/${id}` });

export const saveOpportunity = (data: any) =>
  defHttp.post({ url: API_BASE, data });

export const updateOpportunity = (data: any) =>
  defHttp.put({ url: API_BASE, data });

export const deleteOpportunity = (ids: string) =>
  defHttp.delete({ url: `${API_BASE}/${ids}` });
