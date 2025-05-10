#import "ShareFacebookCallbackPlugin.h"
#if __has_include(<share_facebook_callback/share_facebook_callback-Swift.h>)
#import <share_facebook_callback/share_facebook_callback-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "share_facebook_callback-Swift.h"
#endif

@implementation ShareFacebookCallbackPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftShareFacebookCallbackPlugin registerWithRegistrar:registrar];
}
@end
