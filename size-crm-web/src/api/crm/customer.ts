import { defHttp } from '/@/utils/http/axios';
import { CustomerParams, CustomerModel, CustomerPageListGetResultModel } from './model/customerModel';

enum Api {
  CustomerPage = '/crm/customer/page',
  Customer = '/crm/customer',
}

export const getCustomerList = (params?: CustomerParams) =>
  defHttp.get<CustomerPageListGetResultModel>({ url: Api.CustomerPage, params });

export const getCustomerInfo = (id: string) =>
  defHttp.get<CustomerModel>({ url: `${Api.Customer}/${id}` });

export const saveCustomer = (data: CustomerModel) =>
  defHttp.post<boolean>({ url: Api.Customer, data });

export const updateCustomer = (data: CustomerModel) =>
  defHttp.put<boolean>({ url: Api.Customer, data });

export const deleteCustomer = (id: string) =>
  defHttp.delete<boolean>({ url: `${Api.Customer}/${id}` });
