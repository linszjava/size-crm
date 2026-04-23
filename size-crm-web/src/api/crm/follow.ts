import { defHttp } from '/@/utils/http/axios';

const API_BASE = '/crm/follow';

export const getFollowRecordPage = (params?: Recordable) =>
  defHttp.get({ url: `${API_BASE}/page`, params });

export const getFollowRecord = (id: string) =>
  defHttp.get({ url: `${API_BASE}/${id}` });

export const saveFollowRecord = (data: Recordable) =>
  defHttp.post({ url: API_BASE, data });

export const updateFollowRecord = (data: Recordable) =>
  defHttp.put({ url: API_BASE, data });

export const deleteFollowRecord = (ids: string) =>
  defHttp.delete({ url: `${API_BASE}/${ids}` });

