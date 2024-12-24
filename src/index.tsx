import DynamicIconChange from './NativeDynamicIconChange';

export function getAppIcon(): Promise<string> {
  return DynamicIconChange.getAppIcon();
}

export function changeAppIcon(iconName?: string): Promise<string> {
  return DynamicIconChange.changeAppIcon(iconName);
}
