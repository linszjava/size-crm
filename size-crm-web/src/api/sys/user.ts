import { defHttp } from '/@/utils/http/axios';
import { LoginParams, LoginResultModel, GetUserInfoModel, RegisterParams } from './model/userModel';

import { ErrorMessageMode } from '/#/axios';

enum Api {
  Login = '/auth/login',
  Register = '/auth/register',
  Logout = '/auth/logout',
  GetUserInfo = '/auth/getUserInfo',
  GetPermCode = '/auth/getPermCode',
}

/**
 * @description: user login api
 */
export function loginApi(params: LoginParams, mode: ErrorMessageMode = 'modal') {
  return defHttp.post<LoginResultModel>(
    {
      url: Api.Login,
      params,
    },
    {
      errorMessageMode: mode,
    },
  );
}

export function registerApi(params: RegisterParams, mode: ErrorMessageMode = 'modal') {
  return defHttp.post<boolean>(
    {
      url: Api.Register,
      data: params,
    },
    {
      errorMessageMode: mode,
    },
  );
}

/**
 * @description: getUserInfo
 */
export function getUserInfo() {
  return defHttp.get<GetUserInfoModel>({ url: Api.GetUserInfo }, { errorMessageMode: 'none' });
}

export function getPermCode() {
  return defHttp.get<string[]>({ url: Api.GetPermCode });
}

export function doLogout() {
  return defHttp.get({ url: Api.Logout });
}
