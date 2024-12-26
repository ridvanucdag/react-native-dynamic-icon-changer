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
            moduleImpl.getAppIcon(promise)
    }

  @ReactMethod(isBlockingSynchronousMethod = true)
    fun changeAppIcon(iconName: String, promise: Promise) {
            moduleImpl.changeAppIcon(iconName, promise)
    }
}
