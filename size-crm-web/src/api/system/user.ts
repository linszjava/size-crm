import { defHttp } from '/@/utils/http/axios';

const API_BASE = '/system/user';

export const getUserPage = (params?: any) =>
  defHttp.get({ url: `${API_BASE}/page`, params });

export const getUserList = () =>
  defHttp.get({ url: `${API_BASE}/list` });

export const getUser = (id: string) =>
  defHttp.get({ url: `${API_BASE}/${id}` });

export const saveUser = (data: any) =>
  defHttp.post({ url: API_BASE, data });

export const updateUser = (data: any) =>
  defHttp.put({ url: API_BASE, data });

export const deleteUser = (ids: string) =>
  defHttp.delete({ url: `${API_BASE}/${ids}` });
