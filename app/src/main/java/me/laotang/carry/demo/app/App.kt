package me.laotang.carry.demo.app

import dagger.hilt.android.HiltAndroidApp
import me.laotang.carry.app.CarryApplication

@HiltAndroidApp
class App : CarryApplication() {
    override fun onCreate() {
        super.onCreate()
    }
}