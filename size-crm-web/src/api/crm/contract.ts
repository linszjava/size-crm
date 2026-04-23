import { defHttp } from '/@/utils/http/axios';

const API_BASE = '/crm/contract';

export const getContractPage = (params?: any) =>
  defHttp.get({ url: `${API_BASE}/page`, params });

export const getContract = (id: string) =>
  defHttp.get({ url: `${API_BASE}/${id}` });

export const saveContract = (data: any) =>
  defHttp.post({ url: API_BASE, data });

export const updateContract = (data: any) =>
  defHttp.put({ url: API_BASE, data });

export const deleteContract = (ids: string) =>
  defHttp.delete({ url: `${API_BASE}/${ids}` });

// 提交审批接口
export const submitContractAudit = (id: string) =>
  defHttp.post({ url: `${API_BASE}/submitAudit/${id}` });
