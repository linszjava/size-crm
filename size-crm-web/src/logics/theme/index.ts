import { getThemeColors } from '../../../build/config/themeConfig';

export async function changeTheme(color: string) {
  // Vite 8 cleanup mode:
  // vite-plugin-theme is removed, keep API compatible and switch by CSS var only.
  const html = document.documentElement;
  html.style.setProperty('--primary-color', getThemeColors(color)[0] || color);
  return Promise.resolve();
}
