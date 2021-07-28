package me.laotang.carry.core

import com.laotang.quickdev.rxtray.Preference
import com.laotang.quickdev.rxtray.RxTray
import me.laotang.carry.core.cache.Cache
import me.laotang.carry.core.cache.CacheType
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Retrofit
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.lang.reflect.Type

class RepositoryManager(
    private val retrofit: Retrofit,
    private val rxTray: RxTray,
    cacheFactory: Cache.Factory
) :
    IRepositoryManager {

    private val mRetrofitServiceCache: Cache<String, Any> =
        cacheFactory.build(CacheType.RETROFIT_SERVICE_CACHE)

    @Synchronized
    override fun <T> obtainRetrofitService(service: Class<T>): T {
        return getRetrofitService(service)
    }

    override fun <T> obtainPreferencesService(
        key: String,
        defaultValue: T,
        type: Type
    ): Preference<T> {
        return when (defaultValue) {
            is String -> {
                rxTray.getString(key, defaultValue as String) as Preference<T>
            }
            is Boolean -> {
                rxTray.getBoolean(key, defaultValue as Boolean) as Preference<T>
            }
            is Int -> {
                rxTray.getInteger(key, defaultValue as Int) as Preference<T>
            }
            is Long -> {
                rxTray.getLong(key, defaultValue as Long) as Preference<T>
            }
            is Float -> {
                rxTray.getFloat(key, defaultValue as Float) as Preference<T>
            }
            else -> {
                rxTray.getObject(key, defaultValue, type)
            }
        }
    }

    @Synchronized
    override fun clearAllCache() {
        rxTray.clear()
    }

    private fun <T> createWrapperService(serviceClass: Class<T>): T {
        return Proxy.newProxyInstance(
            serviceClass.classLoader,
            arrayOf<Class<*>>(serviceClass),
            RetrofitServiceProxyHandler(
                retrofit = retrofit,
                serviceClass = serviceClass
            )
        ) as T
    }

    private fun <T> getRetrofitService(serviceClass: Class<T>): T {
        var retrofitService: T? = mRetrofitServiceCache[serviceClass.name] as T
        if (retrofitService == null) {
            retrofitService = createWrapperService(serviceClass)
            mRetrofitServiceCache.put(serviceClass.name, retrofitService!!)
        }
        return retrofitService
    }
}

inline fun <reified T> IRepositoryManager.obtainPreferences(
    key: String,
    defaultValue: T
): Preference<T> {
    return this.obtainPreferencesService(key, defaultValue, T::class.java)
}

class RetrofitServiceProxyHandler(
    private val retrofit: Retrofit,
    private val serviceClass: Class<*>
) : InvocationHandler {

    private var mRetrofitService: Any? = null

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>): Any? {
        return when (method.returnType) {
            Observable::class.java -> {
                Observable.defer { method.invoke(getRetrofitService(), *args) as Observable<*> }
            }
            Single::class.java -> {
                Single.defer { method.invoke(getRetrofitService(), *args) as Single<*> }
            }
            Flowable::class.java -> {
                Flowable.defer { method.invoke(getRetrofitService(), *args) as Flowable<*> }
            }
            else -> method.invoke(getRetrofitService(), *args)
        }
    }

    private fun getRetrofitService(): Any {
        if (mRetrofitService == null) {
            mRetrofitService = retrofit.create(serviceClass)
        }
        return mRetrofitService!!
    }
}