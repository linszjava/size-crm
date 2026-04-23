import { genMessage } from '../helper';
import antdLocale from 'ant-design-vue/es/locale/zh_CN';
import 'moment/dist/locale/zh-cn';

const modules = import.meta.glob('./zh-CN/**/*.ts', { eager: true });
export default {
  message: {
    ...genMessage(modules, 'zh-CN'),
    antdLocale,
  },
  momentLocale: null,
  momentLocaleName: 'zh-cn',
};
