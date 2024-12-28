package com.dynamiciconchange

import android.app.Activity
import android.content.ComponentName
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule

class DynamicIconChangeModuleImpl(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), Application.ActivityLifecycleCallbacks {

    private var activeComponent: String = ""
    private val deactivatedIcons: MutableSet<String> = mutableSetOf()
    private val appPackage: String = reactContext.packageName

    fun getAppIcon(promise: Promise) {
       val activity: Activity? = currentActivity
        if (activity == null) {
            promise.reject("NO_ACTIVE_ACTIVITY", "Activity is null")
            return
        }

        val activityClassName = activity.componentName.className
        if (activityClassName.endsWith("MainActivity")) {
            promise.resolve("Default")
        } else {
            val iconSuffix = activityClassName.split("MainActivity")
            if (iconSuffix.size == 2) {
                promise.resolve(iconSuffix[1])
            } else {
                promise.reject("INVALID_COMPONENT", "Unexpected component class: $activeComponent")
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
            promise.reject("EMPTY_ICON_NAME", "Icon name cannot be empty")
            return
        }

        if (activeComponent.isEmpty()) {
            activeComponent = activity.componentName.className
        }

        val resolvedIcon = if (iconName.isEmpty()) "Default" else iconName
        val newComponent = "$appPackage.MainActivity$resolvedIcon"

        if (activeComponent == newComponent) {
            promise.reject("ICON_ALREADY_USED", "This icon is already active: $activeComponent")
            return
        }

        try {
            activity.packageManager.setComponentEnabledSetting(
                ComponentName(appPackage, newComponent),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            promise.resolve(resolvedIcon)
        } catch (e: Exception) {
            promise.reject("ICON_CHANGE_ERROR", "Icon could not be changed", e)
        }
        deactivatedIcons.add(activeComponent)
        activeComponent = newComponent
        activity.application.registerActivityLifecycleCallbacks(this)
    }

    private fun resetDisabledIcons() {
        val activity: Activity? = currentActivity ?: return
        if (activity == null) return
        deactivatedIcons.remove(activeComponent)
      for (i in deactivatedIcons.indices) {
        val disabledClassName = deactivatedIcons.elementAt(i)
        try {
            activity.packageManager.setComponentEnabledSetting(
                ComponentName(appPackage, disabledClassName),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        } catch (e: Exception) {
            println("Failed to disable icon: $disabledClassName. Error: ${e.localizedMessage}")
        }
    }
    deactivatedIcons.clear()    
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
