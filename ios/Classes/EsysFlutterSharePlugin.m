#import "EsysFlutterSharePlugin.h"
#import <vocsy_esys_flutter_share/vocsy_esys_flutter_share-Swift.h>

@implementation EsysFlutterSharePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftEsysFlutterSharePlugin registerWithRegistrar:registrar];
}
@end
