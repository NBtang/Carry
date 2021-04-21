package me.laotang.carry.core

import android.content.Context
import com.google.gson.JsonDeserializer
import me.laotang.carry.app.AppLifecycleCallbacks
import me.laotang.carry.ManifestDynamicAdapter
import me.laotang.carry.core.cache.Cache
import me.laotang.carry.di.*
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
import me.laotang.carry.core.http.HttpLoggingInterceptor
import okhttp3.HttpUrl
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ConfigModule {

    private var mGlobalConfigModule: GlobalConfigModule? = null
    private var mAppLifecycleCallbacks: MutableList<AppLifecycleCallbacks> = mutableListOf()

    private var initialized: Boolean = false

    @Synchronized
    fun init(context: Context) {
        if (initialized) {
            return
        }
        initialized = true

        val builder =
            GlobalConfigModule.builder()
        val configModules =
            ManifestParser(context).parse()
        for (configModule in configModules) {
            configModule.applyOptions(context, builder)
            configModule.addAppLifecycleCallback(mAppLifecycleCallbacks)
        }
        mGlobalConfigModule = builder.build()
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

}