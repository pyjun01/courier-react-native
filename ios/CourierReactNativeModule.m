#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(CourierReactNativeModule, NSObject)

RCT_EXTERN__BLOCKING_SYNCHRONOUS_METHOD(
  setDebugMode: (BOOL)isDebugging
)

RCT_EXTERN__BLOCKING_SYNCHRONOUS_METHOD(
  iOSForegroundPresentationOptions: (NSDictionary*)params
)

RCT_EXTERN_METHOD(
  signIn: (NSString*)accessToken
  withClientKey: (NSString*)clientKey
  withUserId: (NSString*)userId
  withResolver: (RCTPromiseResolveBlock)resolve
  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(
  signOut: (RCTPromiseResolveBlock)resolve
  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN__BLOCKING_SYNCHRONOUS_METHOD(
  getUserId
)

RCT_EXTERN__BLOCKING_SYNCHRONOUS_METHOD(
  addAuthenticationListener
)

RCT_EXTERN__BLOCKING_SYNCHRONOUS_METHOD(
  removeAuthenticationListener: (NSString*)listenerId
)

RCT_EXTERN_METHOD(
  getNotificationPermissionStatus: (RCTPromiseResolveBlock)resolve
  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(
  requestNotificationPermission: (RCTPromiseResolveBlock)resolve
  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(
  setToken: (NSString*)key
  withToken: (NSString*)token
  withResolver: (RCTPromiseResolveBlock)resolve
  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(
  getToken: (NSString*)key
  withResolver: (RCTPromiseResolveBlock)resolve
  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN__BLOCKING_SYNCHRONOUS_METHOD(
  clickMessage: (NSString*)messageId
)

RCT_EXTERN__BLOCKING_SYNCHRONOUS_METHOD(
  readMessage: (NSString*)messageId
)

RCT_EXTERN__BLOCKING_SYNCHRONOUS_METHOD(
  unreadMessage: (NSString*)messageId
)

RCT_EXTERN_METHOD(
  readAllInboxMessages: (RCTPromiseResolveBlock)resolve
  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN__BLOCKING_SYNCHRONOUS_METHOD(
  addInboxListener
)

RCT_EXTERN__BLOCKING_SYNCHRONOUS_METHOD(
  removeInboxListener: (NSString*)listenerId
)

RCT_EXTERN_METHOD(
  refreshInbox: (RCTPromiseResolveBlock)resolve
  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(
  fetchNextPageOfMessages: (RCTPromiseResolveBlock)resolve
  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN__BLOCKING_SYNCHRONOUS_METHOD(
  setInboxPaginationLimit: (double)limit
)

RCT_EXTERN_METHOD(
  getUserPreferences: (NSString*)paginationCursor
  withResolver: (RCTPromiseResolveBlock)resolve
  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(
  getUserPreferencesTopic: (NSString*)topicId
  withResolver: (RCTPromiseResolveBlock)resolve
  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(
  getUserPreferencesTopic: (NSString*)topicId
  withResolver: (RCTPromiseResolveBlock)resolve
  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(
  putUserPreferencesTopic: (NSString*)topicId
  withStatus: (NSString*)status
  withHasCustomRouting: (BOOL*)hasCustomRouting
  withCustomRouting: (NSArray*)customRouting
  withResolver: (RCTPromiseResolveBlock)resolve
  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(
  registerPushNotificationClickedOnKilledState
)

+ (BOOL)requiresMainQueueSetup
{
  return YES;
}

@end
