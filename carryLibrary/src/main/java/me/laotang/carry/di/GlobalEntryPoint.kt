package me.laotang.carry.di

import android.content.Context
import com.google.gson.Gson
import me.laotang.carry.ManifestDynamicAdapter
import me.laotang.carry.core.imageloader.ImageLoader
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ApplicationComponent
import me.laotang.carry.core.json.JsonConverter
import okhttp3.OkHttpClient
import java.io.File

object GlobalEntryPoint {
    private var entryPoint: IGlobalEntryPoint? = null

    @EntryPoint
    @InstallIn(ApplicationComponent::class)
    interface IGlobalEntryPoint {
        fun imageLoader(): ImageLoader

        fun okHttpClient(): OkHttpClient

        fun gson(): Gson

        fun jsonConverter(): JsonConverter

        @CacheFile
        fun cacheFile(): File

        fun activityConfigAdapter(): ManifestDynamicAdapter.ActivityConfigAdapter?
    }

    fun getImageLoader(context: Context): ImageLoader {
        return getEntryPoint(context)
            .imageLoader()
    }

    fun getActivityConfigAdapter(context: Context): ManifestDynamicAdapter.ActivityConfigAdapter? {
        return getEntryPoint(context)
            .activityConfigAdapter()
    }

    fun getOkHttpClient(context: Context): OkHttpClient {
        return getEntryPoint(context)
            .okHttpClient()
    }

    fun getCacheFile(context: Context): File {
        return getEntryPoint(context)
            .cacheFile()
    }

    fun getGson(context: Context): Gson {
        return getEntryPoint(context)
            .gson()
    }

    fun getJsonConverter(context: Context): JsonConverter {
        return getEntryPoint(context)
            .jsonConverter()
    }

    internal fun getEntryPoint(context: Context): IGlobalEntryPoint {
        if (entryPoint == null) {
            entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                IGlobalEntryPoint::class.java
            )
        }
        return entryPoint!!
    }
}