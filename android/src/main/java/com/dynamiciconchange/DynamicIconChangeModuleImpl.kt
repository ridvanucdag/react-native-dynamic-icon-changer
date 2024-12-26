package com.dynamiciconchange

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule

class DynamicIconChangeModuleImpl(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private var iconChanged = false
    private var componentClass: String = ""
    private val classesToKill = mutableSetOf<String>()

    fun getAppIcon(promise: Promise) {
        val activity: Activity? = currentActivity
        if (activity == null) {
            promise.reject("ANDROID:ACTIVITY_NOT_FOUND", "Activity not found")
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
                promise.reject("ANDROID:UNEXPECTED_COMPONENT_CLASS", "Unexpected class name: $componentClass")
            }
        }
    }

    fun changeAppIcon(iconName: String, promise: Promise) {
        val activity: Activity? = currentActivity
        if (activity == null) {
            promise.reject("ACTIVITY_NOT_FOUND", "The activity is null")
            return
        }
        if (iconName.isNullOrEmpty()) {
            promise.reject("EMPTY_ICON_STRING", "Icon name is missing")
            return
        }

        val packageName = activity.packageName
        val newIconName = iconName
        val activeClass = "$packageName.MainActivity$newIconName"

        if (componentClass == activeClass) {
            promise.reject("ICON_ALREADY_USED", "This icon is already active: $componentClass")
            return
        }

        try {
            activity.packageManager.setComponentEnabledSetting(
                ComponentName(packageName, activeClass),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            val intent = Intent(activity, activity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            activity.startActivity(intent)

            promise.resolve(newIconName)
        } catch (e: Exception) {
            promise.reject("ICON_INVALID", "Failed to change icon", e)
        }

        classesToKill.add(componentClass)
        componentClass = activeClass
        iconChanged = true
    }

    override fun getName(): String {
        return NAME
    }

    companion object {
        const val NAME = "DynamicIconChange"
    }
}
