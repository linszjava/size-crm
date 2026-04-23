import { defHttp } from '/@/utils/http/axios';

const API_BASE = '/workflow/task';

export const getMyTasks = (params?: any) =>
  defHttp.get({ url: `${API_BASE}/myTasks`, params });

export const completeTask = (params: any) =>
  defHttp.post({ url: `${API_BASE}/complete`, data: params });

export const getProcessProgress = (businessKey: string) =>
  defHttp.get({ url: `${API_BASE}/progress/${businessKey}` });
