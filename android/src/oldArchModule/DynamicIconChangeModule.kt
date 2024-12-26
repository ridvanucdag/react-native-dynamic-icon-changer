package com.dynamiciconchange

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactMethod

class DynamicIconChangeModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {
    val moduleImpl = DynamicIconChangeModuleImpl()

  @ReactMethod(isBlockingSynchronousMethod = true)
    fun getAppIcon(promise: Promise) {
        val activity = currentActivity
        if (activity != null) {
            moduleImpl.getAppIcon(activity, promise)
        } else {
            promise.reject("ACTIVITY_NOT_FOUND")
        }
    }

  @ReactMethod(isBlockingSynchronousMethod = true)
    fun changeAppIcon(iconName: String, promise: Promise) {
        val activity = currentActivity
        val packageName = context.packageName
        if (activity != null) {
            moduleImpl.changeAppIcon(activity, packageName, iconName, promise)
        } else {
            promise.reject("ACTIVITY_NOT_FOUND")
        }
    }

  @ReactMethod(isBlockingSynchronousMethod = true)
    fun onActivityPaused(activity: Activity) {
        super.onActivityPaused(activity)
        moduleImpl.onActivityPaused(activity)
    }
}
