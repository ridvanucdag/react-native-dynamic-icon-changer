import { NativeModules, Platform } from 'react-native';
import type { Spec } from './NativeDynamicIconChange';

const LINKING_ERROR =
  `The 'react-native-dynamic-icon-changer' package does not appear to be properly linked. Please ensure the following steps have been completed:\n\n` +
  Platform.select({
    ios: `- Run 'pod install' in the iOS project directory.\n- Make sure to rebuild the app after installation.\n`,
    android: `- Make sure the package is correctly installed in your build.gradle files.\n- Rebuild the app after installation.\n`,
    default: '- Ensure proper installation and rebuild the app.\n',
  }) +
  `If the issue persists, consult the package's documentation or support resources.\n`;

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const DynamicIconChangeModule = isTurboModuleEnabled
  ? require('./NativeDynamicIconChange').default
  : NativeModules.DynamicIconChange;

const DynamicIconChange: Spec = DynamicIconChangeModule
  ? DynamicIconChangeModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export const { getAppIcon, changeAppIcon } = DynamicIconChange;
