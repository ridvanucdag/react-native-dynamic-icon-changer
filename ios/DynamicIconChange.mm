#import "DynamicIconChange.h"

@implementation DynamicIconChange

RCT_EXPORT_MODULE()

+ (BOOL)requiresMainQueueSetup {
    return NO;
}

#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params {
    return std::make_shared<facebook::react::NativeDynamicIconChangeSpecJSI>(params);
}
#endif

RCT_EXPORT_METHOD(getAppIcon:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    dispatch_async(dispatch_get_main_queue(), ^{
        NSString *currentIcon = [[UIApplication sharedApplication] alternateIconName];
        if (currentIcon) {
            resolve(currentIcon);
        } else {
            resolve(@"AppIcon");
        }
    });
}

RCT_EXPORT_METHOD(changeAppIcon:(NSString *)iconName resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([[UIApplication sharedApplication] supportsAlternateIcons] == NO) {
            reject(@"Error", @"IOS:NOT_SUPPORTED", nil);
            return;
        }

        NSString *currentIcon = [[UIApplication sharedApplication] alternateIconName];
        if ([iconName isEqualToString:currentIcon]) {
            reject(@"Error", @"IOS:ICON_ALREADY_USED", nil);
            return;
        }

        NSString *newIconName = (iconName == nil || [iconName length] == 0 || [iconName isEqualToString:@"Default"]) ? nil : iconName;

        [[UIApplication sharedApplication] setAlternateIconName:newIconName completionHandler:^(NSError * _Nullable error) {
            if (error) {
                reject(@"Error", error.localizedDescription, error);
            } else {
                resolve(newIconName ?: @"Default");
            }
        }];
    });
}

@end
