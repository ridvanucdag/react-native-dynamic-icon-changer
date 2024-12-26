package com.dynamiciconchange

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext

class DynamicIconChangeModule(reactContext: ReactApplicationContext) : NativeDynamicIconChangeSpec(reactContext) {

     val moduleImpl = DynamicIconChangeModuleImpl(reactContext)

    override fun getAppIcon(promise: Promise) {
            moduleImpl.getAppIcon(promise)
    }

    override fun changeAppIcon(iconName: String?, promise: Promise) {
            moduleImpl.changeAppIcon(iconName ?: "", promise)
    }
}
