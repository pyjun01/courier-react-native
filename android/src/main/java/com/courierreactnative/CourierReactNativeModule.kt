package com.courierreactnative

import android.content.Intent
import com.courier.android.Courier
import com.courier.android.models.*
import com.courier.android.modules.*
import com.courier.android.utils.pushNotification
import com.courier.android.utils.trackPushNotificationClick
import com.facebook.react.ReactActivity
import com.facebook.react.bridge.*
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.util.*


class CourierReactNativeModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

  override fun getName() = "CourierReactNativeModule"
  private val reactActivity: ReactActivity? get() = currentActivity as? ReactActivity

  // Auth Listeners
  private var authListener: CourierAuthenticationListener? = null
  private var authListeners: MutableList<String> = mutableListOf()

  // Inbox Listeners
  private var inboxListener: CourierInboxListener? = null
  private var inboxListeners: MutableList<String> = mutableListOf()

  init {

    // User Agent is used to ensure we know the SDK
    // the requests come from
    Courier.USER_AGENT = CourierAgent.REACT_NATIVE_ANDROID

    // Attach the log listener
    Courier.shared.logListener = { data ->
      reactContext.sendEvent(CourierEvents.Log.DEBUG_LOG, data)
    }

  }

  @ReactMethod
  fun addListener(type: String?) {
    // Keep: Required for RN built in Event Emitter Calls.
  }

  @ReactMethod
  fun removeListeners(type: Int?) {
    // Keep: Required for RN built in Event Emitter Calls.
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun setDebugMode(isDebugging: Boolean): Boolean {
    Courier.shared.isDebugging = isDebugging
    return Courier.shared.isDebugging
  }

  @ReactMethod
  fun registerPushNotificationClickedOnKilledState() {
    reactActivity?.let { activity ->
      checkIntentForPushNotificationClick(activity.intent)
    }
  }

  private fun checkIntentForPushNotificationClick(intent: Intent?) {
    intent?.trackPushNotificationClick { message ->
      postPushNotificationClicked(message)
    }
  }

  private fun postPushNotificationClicked(message: RemoteMessage) {
    reactApplicationContext.sendEvent(
      eventName = CourierEvents.Push.CLICKED_EVENT,
      value = JSONObject(message.pushNotification).toString()
    )
  }

  @ReactMethod
  fun requestNotificationPermission(promise: Promise) {

    reactActivity?.let { activity ->
      Courier.shared.requestNotificationPermission(activity)
    }

    promise.resolve("unknown")

  }

  @ReactMethod
  fun getNotificationPermissionStatus(promise: Promise) {

    reactActivity?.let { context ->

      val isGranted = Courier.shared.isPushPermissionGranted(context)
      val status = if (isGranted) "authorized" else "denied"
      promise.resolve(status)
      return

    }

    promise.resolve("unknown")

  }

  @ReactMethod
  fun signIn(accessToken: String, clientKey: String?, userId: String, promise: Promise) {
    Courier.shared.signIn(
      accessToken = accessToken,
      clientKey = clientKey,
      userId = userId,
      onSuccess = {
        promise.resolve(null)
      },
      onFailure = { e ->
        promise.reject(CourierEvents.COURIER_ERROR_TAG, e)
      }
    )
  }

  @ReactMethod
  fun signOut(promise: Promise) {
    Courier.shared.signOut(
      onSuccess = {
        promise.resolve(null)
      },
      onFailure = { e ->
        promise.reject(CourierEvents.COURIER_ERROR_TAG, e)
      }
    )
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun getUserId(): String? {
    return Courier.shared.userId
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun addAuthenticationListener(): String {

    authListener?.remove()

    authListener = Courier.shared.addAuthenticationListener { userId ->
      reactApplicationContext.sendEvent(
        eventName = CourierEvents.Auth.USER_CHANGED,
        value = userId
      )
    }

    val id = UUID.randomUUID().toString()
    authListeners.add(id)

    return id

  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun removeAuthenticationListener(listenerId: String): String {

    // Remove the item from the array
    val index = authListeners.indexOf(listenerId)
    if (index != -1) {
      authListeners.removeAt(index)
    }

    // Check the length
    if (authListeners.isEmpty()) {
      authListener?.remove()
      authListener = null
    }

    return listenerId

  }

  @ReactMethod
  fun getToken(key: String, promise: Promise) {
    val token = Courier.shared.getToken(key)
    promise.resolve(token)
  }

  @ReactMethod
  fun setToken(key: String, token: String, promise: Promise) {
    Courier.shared.setToken(
      provider = key,
      token = token,
      onSuccess = {
        promise.resolve(null)
      },
      onFailure = { e ->
        promise.reject(CourierEvents.COURIER_ERROR_TAG, e)
      }
    )
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun clickMessage(messageId: String): String {
    Courier.shared.clickMessage(messageId, onFailure = null)
    return messageId
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun readMessage(messageId: String): String {
    Courier.shared.readMessage(messageId, onFailure = null)
    return messageId
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun unreadMessage(messageId: String): String {
    Courier.shared.unreadMessage(messageId, onFailure = null)
    return messageId
  }

  @ReactMethod
  fun readAllInboxMessages(promise: Promise) {
    Courier.shared.readAllInboxMessages(
      onSuccess = {
        promise.resolve(null)
      },
      onFailure = { e ->
        promise.reject(CourierEvents.COURIER_ERROR_TAG, e)
      }
    )
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun addInboxListener(): String {

    // Remove the old listener
    inboxListener?.remove()

    inboxListener = Courier.shared.addInboxListener(
      onInitialLoad = {
        reactApplicationContext.sendEvent(
          eventName = CourierEvents.Inbox.INITIAL_LOADING,
          value = null
        )
      },
      onError = { e ->
        reactApplicationContext.sendEvent(
          eventName = CourierEvents.Inbox.ERROR,
          value = e.message ?: "Courier Inbox Error"
        )
      },
      onMessagesChanged = { messages: List<InboxMessage>, unreadMessageCount: Int, totalMessageCount: Int, canPaginate: Boolean ->

        val json = Arguments.createMap()
        json.putArray("messages", messages.toWritableArray())
        json.putInt("unreadMessageCount", unreadMessageCount)
        json.putInt("totalMessageCount", totalMessageCount)
        json.putBoolean("canPaginate", canPaginate)

        reactApplicationContext.sendEvent(
          eventName = CourierEvents.Inbox.MESSAGES_CHANGED,
          value = json
        )

      }
    )

    // Add listener
    val id = UUID.randomUUID().toString()
    inboxListeners.add(id)

    return id

  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun removeInboxListener(listenerId: String): String {

    // Remove the item from the array
    val index = inboxListeners.indexOf(listenerId)
    if (index != -1) {
      inboxListeners.removeAt(index)
    }

    // Check the length
    if (inboxListeners.isEmpty()) {
      inboxListener?.remove()
      inboxListener = null
    }

    return listenerId

  }

  @ReactMethod
  fun refreshInbox(promise: Promise) {
    Courier.shared.refreshInbox {
      promise.resolve(null)
    }
  }

  @ReactMethod
  fun fetchNextPageOfMessages(promise: Promise) {
    Courier.shared.fetchNextPageOfMessages(
      onSuccess = { messages ->
        promise.resolve(messages.toWritableArray())
      },
      onFailure = { e ->
        promise.reject(CourierEvents.COURIER_ERROR_TAG, e)
      }
    )
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun setInboxPaginationLimit(limit: Double): String {
    Courier.shared.inboxPaginationLimit = limit.toInt()
    return Courier.shared.inboxPaginationLimit.toString()
  }

  @ReactMethod
  fun getUserPreferences(paginationCursor: String, promise: Promise) {

    val cursor = if (paginationCursor != "") paginationCursor else null

    Courier.shared.getUserPreferences(
      paginationCursor = cursor,
      onSuccess = { preferences ->
        promise.resolve(preferences.toWritableMap())
      },
      onFailure = { e ->
        promise.reject(CourierEvents.COURIER_ERROR_TAG, e)
      }
    )

  }

  @ReactMethod
  fun getUserPreferencesTopic(topicId: String, promise: Promise) {
    Courier.shared.getUserPreferenceTopic(
      topicId = topicId,
      onSuccess = { topic ->
        promise.resolve(topic.toWritableMap())
      },
      onFailure = { e ->
        promise.reject(CourierEvents.COURIER_ERROR_TAG, e)
      }
    )
  }

  @ReactMethod
  fun putUserPreferencesTopic(topicId: String, status: String, hasCustomRouting: Boolean, customRouting: ReadableArray, promise: Promise) {

    val routing = customRouting.toArrayList().map { CourierPreferenceChannel.fromString(it as String) }

    Courier.shared.putUserPreferenceTopic(
      topicId = topicId,
      status = CourierPreferenceStatus.fromString(status),
      hasCustomRouting = hasCustomRouting,
      customRouting = routing,
      onSuccess = {
        promise.resolve(null)
      },
      onFailure = { e ->
        promise.reject(CourierEvents.COURIER_ERROR_TAG, e)
      }
    )

  }

}
