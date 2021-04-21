package me.laotang.carry

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import me.laotang.carry.core.ConfigModule
import es.dmoral.toasty.Toasty

class SupportInitialization : Initializer<Unit> {
    override fun create(context: Context) {
        AppManager.instance.init(context)
        ConfigModule.init(context)
        (context as Application).registerActivityLifecycleCallbacks(ManifestDynamicAdapter())
        Toasty.Config.getInstance().allowQueue(false).apply()
    }

    override fun dependencies(): List<Class<Initializer<*>>> {
        return emptyList()
    }
}