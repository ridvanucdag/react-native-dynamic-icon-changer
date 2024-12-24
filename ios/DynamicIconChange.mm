#import "DynamicIconChange.h"

@implementation DynamicIconChange

RCT_EXPORT_MODULE()

+ (BOOL)requiresMainQueueSetup {
    return NO;
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params {
    return std::make_shared<facebook::react::NativeDynamicIconChangeSpecJSI>(params);
}

- (void)getAppIcon:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
    dispatch_async(dispatch_get_main_queue(), ^{
        NSString *currentIcon = [[UIApplication sharedApplication] alternateIconName];
        if (currentIcon) {
            resolve(currentIcon);
        } else {
            resolve(@"AppIcon");
        }
    });
}


- (void)changeAppIcon:(NSString *)iconName resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
    dispatch_async(dispatch_get_main_queue(), ^{
        NSError *error = nil;

        if ([[UIApplication sharedApplication] supportsAlternateIcons] == NO) {
            reject(@"Error", @"IOS:NOT_SUPPORTED", nil);
            return;
        }

        NSString *currentIcon = [[UIApplication sharedApplication] alternateIconName];

        if ([iconName isEqualToString:currentIcon]) {
            reject(@"Error", @"IOS:ICON_ALREADY_USED", nil);
            return;
        }

        NSString *newIconName;
        if (iconName == nil || [iconName length] == 0 || [iconName isEqualToString:@"Default"]) {
            newIconName = nil;
            resolve(@"Default");
        } else {
            newIconName = iconName;
            resolve(newIconName);
        }

        [[UIApplication sharedApplication] setAlternateIconName:newIconName completionHandler:^(NSError * _Nullable error) {
            if (error) {
                NSString *errorDescription = [NSString stringWithUTF8String:[error.localizedDescription UTF8String]];
                reject(@"Error", errorDescription, error);
            }
        }];
    });
}

@end
