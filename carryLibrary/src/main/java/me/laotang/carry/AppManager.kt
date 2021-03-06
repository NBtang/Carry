package me.laotang.carry

import android.app.Activity
import android.app.Application
import android.content.Context
import java.util.*
import kotlin.system.exitProcess

class AppManager private constructor() {
    private lateinit var mApplicationContext: Context
    private var mActivityList: LinkedList<Activity> = LinkedList()
    private var mCurrentActivity: Activity? = null

    private var initialized: Boolean = false

    internal fun init(application: Context): AppManager {
        if (this.initialized) {
            return instance
        }
        this.initialized = true
        this.mApplicationContext = application
        (this.mApplicationContext as Application).registerActivityLifecycleCallbacks(ActivityLifecycle())
        return instance
    }

    fun setCurrentActivity(currentActivity: Activity?) {
        this.mCurrentActivity = currentActivity
    }

    fun getCurrentActivity(): Activity? {
        return mCurrentActivity
    }

    fun getApplicationContext(): Context {
        return this.mApplicationContext
    }

    fun getTopActivity(): Activity? {
        getActivityList().apply {
            return if (size > 0) get(size - 1) else null
        }
    }

    fun getActivityList(): LinkedList<Activity> {
        return mActivityList
    }

    fun addActivity(activity: Activity) {
        synchronized(AppManager::class.java) {
            val activities = getActivityList()
            if (!activities.contains(activity)) {
                activities.add(activity)
            }
        }
    }

    fun removeActivity(activity: Activity) {
        getActivityList().apply {
            synchronized(AppManager::class.java) {
                if (contains(activity)) {
                    remove(activity)
                }
            }
        }
    }

    fun removeActivity(location: Int): Activity? {
        getActivityList().apply {
            synchronized(AppManager::class.java) {
                if (location in 1 until size) {
                    return removeAt(location)
                }
            }
        }
        return null
    }

    fun killActivity(activityClass: Class<*>) {
        getActivityList().apply {
            synchronized(AppManager::class.java) {
                val iterator = getActivityList().iterator()
                while (iterator.hasNext()) {
                    val next = iterator.next()

                    if (next.javaClass == activityClass) {
                        iterator.remove()
                        next.finish()
                    }
                }
            }
        }
    }

    fun activityInstanceIsLive(activity: Activity): Boolean {
        getActivityList().apply { return contains(activity) }
    }

    fun activityClassIsLive(activityClass: Class<*>): Boolean {
        getActivityList().apply {
            for (activity in this) {
                if (activity.javaClass == activityClass) {
                    return true
                }
            }
            return false
        }
    }

    fun findActivity(activityClass: Class<*>): Activity? {
        getActivityList().apply {
            for (activity in this) {
                if (activity.javaClass == activityClass) {
                    return activity
                }
            }
        }
        return null
    }

    fun killAll() {
        synchronized(AppManager::class.java) {
            val iterator = getActivityList().iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                iterator.remove()
                next.finish()
            }
        }
    }

    fun killAll(vararg excludeActivityClasses: Class<*>) {
        val excludeList = mutableListOf(*excludeActivityClasses)
        synchronized(AppManager::class.java) {
            val iterator = getActivityList().iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()

                if (excludeList.contains(next.javaClass))
                    continue

                iterator.remove()
                next.finish()
            }
        }
    }

    fun killAll(vararg excludeActivityName: String) {
        val excludeList = mutableListOf(*excludeActivityName)
        synchronized(AppManager::class.java) {
            val iterator = getActivityList().iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()

                if (excludeList.contains(next.javaClass.name))
                    continue

                iterator.remove()
                next.finish()
            }
        }
    }

    fun appExit() {
        try {
            killAll()
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {
        val instance: AppManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AppManager()
        }
    }
}