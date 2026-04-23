import { defHttp } from '/@/utils/http/axios';

const API_BASE = '/system/dept';

export const getDeptList = (params?: any) =>
  defHttp.get({ url: `${API_BASE}/list`, params });

export const getDept = (id: string) =>
  defHttp.get({ url: `${API_BASE}/${id}` });

export const saveDept = (data: any) =>
  defHttp.post({ url: API_BASE, data });

export const updateDept = (data: any) =>
  defHttp.put({ url: API_BASE, data });

export const deleteDept = (id: string) =>
  defHttp.delete({ url: `${API_BASE}/${id}` });
