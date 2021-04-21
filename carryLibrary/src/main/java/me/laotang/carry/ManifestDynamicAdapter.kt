package me.laotang.carry

import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import me.laotang.carry.di.GlobalEntryPoint

class ManifestDynamicAdapter : Application.ActivityLifecycleCallbacks {

    private var screenOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        requestedOrientation(activity)
        GlobalEntryPoint.getActivityConfigAdapter(activity)?.onCreated(activity, savedInstanceState)
    }

    private fun requestedOrientation(activity: Activity) {
        if (activity.javaClass.isAnnotationPresent(Orientation::class.java)) {
            val orientation = activity.javaClass.getAnnotation(Orientation::class.java)!!
            activity.requestedOrientation = orientation.value
        } else {
            activity.requestedOrientation = screenOrientation
        }
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        GlobalEntryPoint.getActivityConfigAdapter(activity)?.onResumed(activity)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    interface ActivityConfigAdapter {
        fun onCreated(activity: Activity, savedInstanceState: Bundle?)
        fun onResumed(activity: Activity)
    }
}

