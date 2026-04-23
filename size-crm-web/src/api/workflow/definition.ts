import { defHttp } from '/@/utils/http/axios';

const API_BASE = '/workflow/definition';
const FALLBACK_API_BASE = '/definition';

function isNotFoundError(error: any) {
  const status = error?.response?.status ?? error?.status;
  return status === 404;
}

async function withFallback<T>(path: string) {
  try {
    return await defHttp.get<T>({ url: `${API_BASE}${path}` });
  } catch (error) {
    if (!isNotFoundError(error)) {
      throw error;
    }
    return await defHttp.get<T>({ url: `${FALLBACK_API_BASE}${path}` });
  }
}

export const getProcessDefinitionList = () => withFallback<any[]>('/list');

export const getProcessDefinitionXml = (definitionId: string) =>
  withFallback<string>(`/xml/${definitionId}`);

