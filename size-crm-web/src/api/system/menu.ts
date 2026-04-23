import { defHttp } from '/@/utils/http/axios';

const API_BASE = '/system/menu';

export const getMenuList = (params?: any) =>
  defHttp.get({ url: `${API_BASE}/list`, params });

export const getMenu = (id: string) =>
  defHttp.get({ url: `${API_BASE}/${id}` });

export const saveMenu = (data: any) =>
  defHttp.post({ url: API_BASE, data });

export const updateMenu = (data: any) =>
  defHttp.put({ url: API_BASE, data });

export const deleteMenu = (id: string) =>
  defHttp.delete({ url: `${API_BASE}/${id}` });
