#ifdef RCT_NEW_ARCH_ENABLED
#import "generated/RNDynamicIconChangeSpec/RNDynamicIconChangeSpec.h"
@interface DynamicIconChange : NSObject <NativeDynamicIconChangeSpec>
#else
#import <React/RCTBridgeModule.h>
@interface DynamicIconChange : NSObject <RCTBridgeModule>
#endif

@end
