package me.laotang.carry.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import me.laotang.carry.di.GlobalComponent

open class CarryApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        GlobalComponent.init(this)
        GlobalComponent.getAppLifecycleCallbacks().forEach {
            it.attachBaseContext(base, this)
        }
    }

    override fun onCreate() {
        super.onCreate()
        GlobalComponent.getAppLifecycleCallbacks().forEach {
            it.onCreate(this)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        GlobalComponent.getAppLifecycleCallbacks().forEach {
            it.onLowMemory(this)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        GlobalComponent.getAppLifecycleCallbacks().forEach {
            it.onConfigurationChanged(newConfig, this)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        GlobalComponent.getAppLifecycleCallbacks().forEach {
            it.onTerminate(this)
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        GlobalComponent.getAppLifecycleCallbacks().forEach {
            it.onTrimMemory(level, this)
        }
    }
}