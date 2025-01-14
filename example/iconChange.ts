import { Platform } from 'react-native';
import { changeAppIcon, getAppIcon } from 'react-native-dynamic-icon-changer';

export enum IconNames {
  AppIcon = 'Default',
  AppIcon2 = 'AppIcon2',
  AppIcon3 = 'AppIcon3',
  MainApplication = 'Default',
  MainApplication2 = 'Icon2',
  MainApplication3 = 'Icon3',
}

export const getIconName = (iconKey: number): string | null => {
  switch (iconKey) {
    case 1:
      return Platform.OS === 'android'
        ? IconNames.MainApplication
        : IconNames.AppIcon;
    case 2:
      return Platform.OS === 'android'
        ? IconNames.MainApplication2
        : IconNames.AppIcon2;
    case 3:
      return Platform.OS === 'android'
        ? IconNames.MainApplication3
        : IconNames.AppIcon3;
    default:
      return Platform.OS === 'android'
        ? IconNames.MainApplication
        : IconNames.AppIcon;
  }
};

export const iconChange = async (iconKey: number): Promise<void> => {
  const iconName = getIconName(iconKey);
  try {
    getAppIcon()
      .then((currentIcon: string) => {
        if (currentIcon !== iconName && iconName) {
          changeAppIcon(iconName);
        }
      })
      .catch((error: Error) => {
        console.error('Error fetching current icon:', JSON.stringify(error));
      });
  } catch (error) {
    console.error(JSON.stringify(error));
  }
};
