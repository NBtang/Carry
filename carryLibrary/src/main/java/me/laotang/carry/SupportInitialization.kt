package me.laotang.carry

import android.content.Context
import androidx.startup.Initializer
import me.laotang.carry.di.GlobalComponent
import es.dmoral.toasty.Toasty

class SupportInitialization : Initializer<Unit> {
    override fun create(context: Context) {
        AppManager.instance.init(context)
        GlobalComponent.init(context)
        Toasty.Config.getInstance().allowQueue(false).apply()
    }

    override fun dependencies(): List<Class<Initializer<*>>> {
        return emptyList()
    }
}