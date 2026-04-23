import { defHttp } from '/@/utils/http/axios';
import { ContactParams, ContactModel, ContactPageListGetResultModel } from './model/contactModel';

enum Api {
  ContactPage = '/crm/contact/page',
  Contact     = '/crm/contact',
}

/** 分页查询联系人列表 */
export const getContactList = (params?: ContactParams) =>
  defHttp.get<ContactPageListGetResultModel>({ url: Api.ContactPage, params });

/** 获取联系人详情 */
export const getContactInfo = (id: string) =>
  defHttp.get<ContactModel>({ url: `${Api.Contact}/${id}` });

/** 新增联系人 */
export const saveContact = (data: Partial<ContactModel>) =>
  defHttp.post<boolean>({ url: Api.Contact, data });

/** 修改联系人 */
export const updateContact = (data: Partial<ContactModel>) =>
  defHttp.put<boolean>({ url: Api.Contact, data });

/** 删除联系人 */
export const deleteContact = (id: string) =>
  defHttp.delete<boolean>({ url: `${Api.Contact}/${id}` });
