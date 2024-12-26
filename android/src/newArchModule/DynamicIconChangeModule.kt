package com.dynamiciconchange

import android.app.Activity
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext

class DynamicIconChangeModule(context: ReactApplicationContext) : NativeDynamicIconChangeSpec(context) {

    private val moduleImpl = DynamicIconChangeModuleImpl()

    override fun getAppIcon(promise: Promise) {
        val activity = currentActivity
        if (activity != null) {
            moduleImpl.getAppIcon(activity, promise)
        } else {
            promise.reject("ACTIVITY_NOT_FOUND", "Activity not found")
        }
    }

    override fun changeAppIcon(iconName: String, promise: Promise) {
        val activity = currentActivity
        val packageName = context.packageName
        if (activity != null) {
            moduleImpl.changeAppIcon(activity, packageName, iconName, promise)
        } else {
            promise.reject("ACTIVITY_NOT_FOUND", "Activity not found")
        }
    }

    override fun onActivityPaused(activity: Activity) {
        super.onActivityPaused(activity)
        moduleImpl.onActivityPaused(activity)
    }
}
