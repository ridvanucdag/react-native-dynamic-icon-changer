import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  getAppIcon(): Promise<string>;
  changeAppIcon(iconName?: string | null): Promise<string>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('DynamicIconChange');
