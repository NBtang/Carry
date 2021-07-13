package me.laotang.carry.di

import android.content.Context
import android.text.TextUtils
import androidx.annotation.Keep
import com.google.gson.JsonDeserializer
import me.laotang.carry.app.AppLifecycleCallbacks
import me.laotang.carry.ManifestDynamicAdapter
import me.laotang.carry.core.cache.Cache
import me.laotang.carry.core.http.response.BaseResponseBean
import me.laotang.carry.core.http.response.BaseResponseBeanDeserializer
import me.laotang.carry.core.imageloader.ImageLoaderStrategy
import me.laotang.carry.core.imageloader.ImageLoaderViewTarget
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener
import me.laotang.carry.core.IConfigModule
import me.laotang.carry.core.IRepositoryManager
import me.laotang.carry.core.ManifestParser
import me.laotang.carry.core.http.HttpLoggingInterceptor
import me.laotang.carry.core.json.JsonConverter
import okhttp3.HttpUrl
import java.io.File
import java.lang.reflect.InvocationTargetException
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object GlobalComponent {

    private var mGlobalConfigModule: GlobalConfigModule? = null
    private var mAppLifecycleCallbacks: MutableList<AppLifecycleCallbacks> = mutableListOf()

    private var mAutoRegisterModules: MutableList<IConfigModule>? = null

    private var initialized: Boolean = false

    @Synchronized
    fun init(context: Context) {
        if (initialized) {
            return
        }
        initialized = true
        loadAutoRegister()
        val builder =
            GlobalConfigModule.builder()
        val configModules = mAutoRegisterModules ?: ManifestParser(context).parse()
        for (configModule in configModules) {
            configModule.applyOptions(context, builder)
            configModule.addAppLifecycleCallback(mAppLifecycleCallbacks)
        }
        mGlobalConfigModule = builder.build()
        mAutoRegisterModules = null
    }

    @Keep
    private fun loadAutoRegister() {

    }

    @Keep
    private fun register(className: String) {
        if (!TextUtils.isEmpty(className)) {
            try {
                val clazz = Class.forName(className)
                val obj = clazz.getConstructor().newInstance()
                if (obj is IConfigModule) {
                    mAutoRegisterModules = (mAutoRegisterModules ?: mutableListOf())
                    mAutoRegisterModules?.add(obj)
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }

        }
    }

    internal fun getAppLifecycleCallbacks(): List<AppLifecycleCallbacks> {
        return mAppLifecycleCallbacks
    }

    internal fun addAppLifecycleCallback(vararg lifecycleCallbacks: AppLifecycleCallbacks) {
        mAppLifecycleCallbacks.addAll(lifecycleCallbacks)
    }

    @Singleton
    @Provides
    fun provideConfigModuleManage(): GlobalConfigModule {
        if (mGlobalConfigModule == null) {
            mGlobalConfigModule = GlobalConfigModule.builder()
                .build()
        }
        return mGlobalConfigModule!!
    }

    @Singleton
    @Provides
    fun provideBaseUrl(
        globalConfigModule: GlobalConfigModule
    ): HttpUrl {
        return globalConfigModule.provideBaseUrl()
    }

    @Singleton
    @Provides
    @CacheFile
    fun provideCacheFile(
        @ApplicationContext context: Context,
        globalConfigModule: GlobalConfigModule
    ): File {
        return globalConfigModule.provideCacheFile(context)
    }

    @Singleton
    @Provides
    fun provideCacheFactory(
        @ApplicationContext context: Context,
        globalConfigModule: GlobalConfigModule
    ): Cache.Factory {
        return globalConfigModule.provideCacheFactory(context)
    }

    @Singleton
    @Provides
    fun provideRetrofitConfigurations(
        globalConfigModule: GlobalConfigModule
    ): RetrofitConfiguration? {
        return globalConfigModule.provideRetrofitConfiguration()
    }

    @Singleton
    @Provides
    fun provideOkHttpConfigurations(
        globalConfigModule: GlobalConfigModule
    ): OkHttpConfiguration? {
        return globalConfigModule.provideOkHttpConfiguration()
    }

    @Singleton
    @Provides
    fun provideResponseErrorListener(
        globalConfigModule: GlobalConfigModule
    ): ResponseErrorListener {
        return globalConfigModule.provideResponseErrorListener()
    }

    @Singleton
    @Provides
    fun provideGsonConfiguration(
        globalConfigModule: GlobalConfigModule
    ): GsonConfiguration? {
        return globalConfigModule.provideGsonConfiguration()
    }


    @Singleton
    @Provides
    fun provideRxProgressConfiguration(
        globalConfigModule: GlobalConfigModule
    ): RxProgressConfiguration {
        return globalConfigModule.provideRxProgressConfiguration()
    }

    @Singleton
    @Provides
    fun provideImageLoaderStrategy(
        globalConfigModule: GlobalConfigModule
    ): ImageLoaderStrategy<ImageLoaderViewTarget<*>>? {
        return globalConfigModule.provideImageLoaderStrategy()
    }

    @Singleton
    @Provides
    fun provideImageLoaderConfiguration(
        globalConfigModule: GlobalConfigModule
    ): ImageLoaderConfiguration? {
        return globalConfigModule.provideImageLoaderConfiguration()
    }

    @Singleton
    @Provides
    fun provideResponseDeserializer(
        globalConfigModule: GlobalConfigModule
    ): JsonDeserializer<BaseResponseBean<*>> {
        return globalConfigModule.provideResponseDeserializer() ?: BaseResponseBeanDeserializer()
    }

    @Singleton
    @Provides
    fun provideActivityConfigAdapter(
        globalConfigModule: GlobalConfigModule
    ): ManifestDynamicAdapter.ActivityConfigAdapter? {
        return globalConfigModule.provideActivityConfigAdapter()
    }

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(
        globalConfigModule: GlobalConfigModule
    ): HttpLoggingInterceptor? {
        return globalConfigModule.provideHttpLoggingInterceptor()
    }

    @Singleton
    @Provides
    fun provideRepositoryManager(
        @EmbedRepositoryManager repositoryManager: IRepositoryManager,
        globalConfigModule: GlobalConfigModule
    ): IRepositoryManager {
        return globalConfigModule.provideRepositoryManager() ?: repositoryManager
    }

    @Singleton
    @Provides
    fun provideJsonConverter(
        @EmbedJsonConverter jsonConverter: JsonConverter,
        globalConfigModule: GlobalConfigModule
    ): JsonConverter {
        return globalConfigModule.provideJsonConverter() ?: jsonConverter
    }

}