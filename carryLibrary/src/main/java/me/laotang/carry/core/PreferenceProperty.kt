package me.laotang.carry.core

import androidx.lifecycle.LiveData
import com.laotang.quickdev.rxtray.Preference
import com.laotang.quickdev.rxtray.RxTray
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import me.laotang.carry.AppManager
import me.laotang.carry.util.globalEntryPoint
import java.lang.reflect.Type
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


inline fun <reified T> preference(name: String, default: T): PreferenceProperty<T> {
    return PreferenceProperty(name, default, T::class.java)
}

inline fun <reified T> preferenceLiveData(name: String, default: T): LiveData<T> {
    return preference(name, default).asLiveData()
}

inline fun <reified T> preferenceObservable(name: String, default: T): Observable<T> {
    return preference(name, default).asObservable()
}

/**
 * 以委托的形式，提供缓存数据
 * 以正常的赋值操作即可更新缓存数据
 */
class PreferenceProperty<T>(
    private val name: String,
    private val default: T,
    private val type: Type
) :
    ReadWriteProperty<Any?, T> {

    private val preference: Preference<T> by lazy {
        getPreferenceImpl() as Preference<T>
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        preference.set(value)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return preference.get()
    }

    fun asObservable(): Observable<T> {
        return preference.asObservable()
    }

    fun asLiveData(): LiveData<T> {
        return PreferenceLiveData(preference)
    }

    private fun getPreferenceImpl(): Preference<*> {
        val rxTray: RxTray = AppManager.instance.getApplicationContext().globalEntryPoint.rxTray()
        return when (default) {
            is String -> {
                rxTray.getString(name, default as String)
            }
            is Boolean -> {
                rxTray.getBoolean(name, default as Boolean)
            }
            is Int -> {
                rxTray.getInteger(name, default as Int)
            }
            is Long -> {
                rxTray.getLong(name, default as Long)
            }
            is Float -> {
                rxTray.getFloat(name, default as Float)
            }
            else -> {
                rxTray.getObject(name, default, type)
            }
        }
    }
}

/**
 * 以liveData的形式提供数据
 */
internal class PreferenceLiveData<T>(private val preference: Preference<T>) :
    LiveData<T>(), Consumer<T> {

    private var disposable: Disposable? = null

    init {
        value = preference.get()
    }

    override fun onActive() {
        super.onActive()
        disposable = preference.asObservable().subscribe(this)
    }

    override fun onInactive() {
        super.onInactive()
        disposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        disposable = null
    }

    /**
     * 只有value不同的情况下，才更新liveData
     */
    override fun accept(t: T) {
        if (value != t)
            value = t
    }
}