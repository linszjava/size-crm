import { defHttp } from '/@/utils/http/axios';

const API_BASE = '/workflow/task';
const FALLBACK_API_BASE = '/task';

function isNotFoundError(error: any) {
  const status = error?.response?.status ?? error?.status;
  return status === 404;
}

async function getWithFallback<T>(path: string, params?: any) {
  try {
    return await defHttp.get<T>({ url: `${API_BASE}${path}`, params });
  } catch (error) {
    if (!isNotFoundError(error)) {
      throw error;
    }
    return await defHttp.get<T>({ url: `${FALLBACK_API_BASE}${path}`, params });
  }
}

async function postWithFallback<T>(path: string, data?: any) {
  try {
    return await defHttp.post<T>({ url: `${API_BASE}${path}`, data });
  } catch (error) {
    if (!isNotFoundError(error)) {
      throw error;
    }
    return await defHttp.post<T>({ url: `${FALLBACK_API_BASE}${path}`, data });
  }
}

export const getMyTasks = (params?: { userId?: string; roleKeys?: string[] }) => {
  const { userId, roleKeys } = params || {};
  const query: Recordable = { userId };
  if (roleKeys?.length) {
    query.roleKeysCsv = roleKeys.join(',');
  }
  return getWithFallback<any[]>('/myTasks', query);
};

export const completeTask = (params: any) =>
  postWithFallback<boolean>('/complete', params);

export const getProcessProgress = (businessKey: string) =>
  getWithFallback<any[]>(`/progress/${businessKey}`);
