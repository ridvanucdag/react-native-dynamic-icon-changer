package com.dynamiciconchange

import android.app.Activity
import android.content.ComponentName
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule

class DynamicIconChangeModuleImpl(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), Application.ActivityLifecycleCallbacks {

    private var componentClass: String = ""
    private val disabledComponents: MutableSet<String> = mutableSetOf()
    private val packageName: String = reactContext.packageName
    private var iconChanged: Boolean = false

    fun getAppIcon(promise: Promise) {
        val activity: Activity? = currentActivity
        if (activity == null) {
            promise.reject("ACTIVITY_NOT_FOUND", "Activity not found")
            return
        }

        val activityName = activity.componentName.className
        if (activityName.endsWith("MainActivity")) {
            promise.resolve("Default")
        } else {
            val activityNameSplit = activityName.split("MainActivity")
            if (activityNameSplit.size == 2) {
                promise.resolve(activityNameSplit[1])
            } else {
                promise.reject("UNEXPECTED_COMPONENT_CLASS", "Unexpected class name: $componentClass")
            }
        }
    }

    fun changeAppIcon(iconName: String, promise: Promise) {
        val activity: Activity? = currentActivity
        if (activity == null) {
            promise.reject("ACTIVITY_NOT_FOUND", "The activity is null")
            return
        }
        if (iconName.isEmpty()) {
            promise.reject("EMPTY_ICON_STRING", "Icon name is empty")
            return
        }

        if (componentClass.isEmpty()) {
            componentClass = activity.componentName.className
        }
        
        val newIconName = if (iconName.isEmpty()) "Default" else iconName
        val newComponentClass = "$packageName.MainActivity$newIconName"

        if (componentClass == newComponentClass) {
            promise.reject("ICON_ALREADY_USED", "This icon is already active: $componentClass")
            return
        }

        try {
            activity.packageManager.setComponentEnabledSetting(
                ComponentName(packageName, newComponentClass),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

        disabledComponents.add(componentClass)
        componentClass = newComponentClass
 
            val intent = Intent(activity, activity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            activity.startActivity(intent)

            promise.resolve(newIconName)
        } catch (e: Exception) {
            promise.reject("ICON_INVALID", "Icon could not be changed", e)
        }

        iconChanged = true
        activity.application.registerActivityLifecycleCallbacks(this)
    }

    private fun resetDisabledIcons() {
        if (!iconChanged) return
        val activity: Activity? = currentActivity ?: return
        if (activity == null) return
        disabledComponents.remove(componentClass)
    disabledComponents.forEach { disabledClassName ->
        activity.packageManager.setComponentEnabledSetting(
            ComponentName(packageName, disabledClassName),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }
        disabledComponents.clear()
        iconChanged = false
    }

    override fun onActivityPaused(activity: Activity) {
        resetDisabledIcons()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {
        resetDisabledIcons()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    override fun getName(): String {
        return NAME
    }

    companion object {
        const val NAME = "DynamicIconChange"
    }
}
