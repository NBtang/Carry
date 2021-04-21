package me.laotang.carry.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import me.laotang.carry.core.ConfigModule

open class CarryApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        ConfigModule.init(this)
        ConfigModule.getAppLifecycleCallbacks().forEach {
            it.attachBaseContext(base, this)
        }
    }

    override fun onCreate() {
        super.onCreate()
        ConfigModule.getAppLifecycleCallbacks().forEach {
            it.onCreate(this)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        ConfigModule.getAppLifecycleCallbacks().forEach {
            it.onLowMemory(this)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        ConfigModule.getAppLifecycleCallbacks().forEach {
            it.onConfigurationChanged(newConfig, this)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        ConfigModule.getAppLifecycleCallbacks().forEach {
            it.onTerminate(this)
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        ConfigModule.getAppLifecycleCallbacks().forEach {
            it.onTrimMemory(level, this)
        }
    }
}