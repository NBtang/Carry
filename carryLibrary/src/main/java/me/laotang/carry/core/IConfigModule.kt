package me.laotang.carry.core

import android.content.Context
import me.laotang.carry.app.AppLifecycleCallbacks
import me.laotang.carry.di.GlobalConfigModule

interface IConfigModule {
    fun applyOptions(
        context: Context,
        builder: GlobalConfigModule.Builder
    )

    fun addAppLifecycleCallback(lifecycleCallbacks: MutableList<AppLifecycleCallbacks>) {}

}