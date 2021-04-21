package me.laotang.carry

import android.content.Context
import me.laotang.carry.app.AppLifecycleCallbacks
import me.laotang.carry.core.IConfigModule
import me.laotang.carry.di.GlobalConfigModule

class AppConfigModule: IConfigModule {
    override fun applyOptions(context: Context, builder: GlobalConfigModule.Builder) {
        builder.baseUrl("https://api.github.com/")
    }

    override fun addAppLifecycleCallback(lifecycleCallbacks: MutableList<AppLifecycleCallbacks>) {
        lifecycleCallbacks.add(AppLifecycleCallbacksImpl())
    }
}