import { defHttp } from '/@/utils/http/axios';

const API_BASE = '/system/role';

export const getRolePage = (params?: any) =>
  defHttp.get({ url: `${API_BASE}/page`, params });

export const getRoleList = () =>
  defHttp.get({ url: `${API_BASE}/list` });

export const getRole = (id: string) =>
  defHttp.get({ url: `${API_BASE}/${id}` });

export const saveRole = (data: any) =>
  defHttp.post({ url: API_BASE, data });

export const updateRole = (data: any) =>
  defHttp.put({ url: API_BASE, data });

export const deleteRole = (ids: string) =>
  defHttp.delete({ url: `${API_BASE}/${ids}` });
