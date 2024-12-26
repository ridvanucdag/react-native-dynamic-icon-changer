package com.dynamiciconchange

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.facebook.react.bridge.Promise

class DynamicIconChangeModuleImpl {

    private var iconChanged = false
    private var componentClass: String = ""
    private val classesToKill = mutableSetOf<String>()

    fun getAppIcon(activity: Activity, promise: Promise) {
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

    fun changeAppIcon(activity: Activity, packageName: String, iconName: String?, promise: Promise) {
        if (activity == null) {
            promise.reject("ACTIVITY_NOT_FOUND", "The activity is null")
            return
        }
        if (iconName.isNullOrEmpty()) {
            promise.reject("EMPTY_ICON_STRING", "Icon name is missing")
            return
        }

        val newIconName = iconName ?: "Default"
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

            val intent = Intent(activity, activity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)

            val activityManager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.restartPackage(activity.packageName)

            promise.resolve(newIconName)
        } catch (e: Exception) {
            promise.reject("ICON_INVALID", "Failed to change icon", e)
        }

        classesToKill.add(componentClass)
        componentClass = activeClass
        iconChanged = true
    }

    fun completeIconChange(activity: Activity) {
        if (!iconChanged) return

        classesToKill.remove(componentClass)
        classesToKill.forEach {
            activity.packageManager.setComponentEnabledSetting(
                ComponentName(activity.packageName, it),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }
        classesToKill.clear()
        iconChanged = false
    }

    fun onActivityPaused(activity: Activity) {
        completeIconChange(activity)
    }

    companion object {
        const val NAME = "DynamicIconChange"
    }
}
