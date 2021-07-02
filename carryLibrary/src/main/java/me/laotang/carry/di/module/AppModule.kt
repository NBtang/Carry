package me.laotang.carry.di.module

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.laotang.quickdev.rxtray.Converter
import com.laotang.quickdev.rxtray.RxTray
import me.laotang.carry.AppManager
import me.laotang.carry.core.IRepositoryManager
import me.laotang.carry.core.RepositoryManager
import me.laotang.carry.core.cache.Cache
import me.laotang.carry.di.GsonConfiguration
import me.laotang.carry.di.ImageLoaderConfiguration
import me.laotang.carry.core.http.response.BaseResponseBean
import me.laotang.carry.core.imageloader.ImageLoader
import me.laotang.carry.core.imageloader.ImageLoaderStrategy
import me.laotang.carry.core.imageloader.ImageLoaderViewTarget
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import me.laotang.carry.core.json.GsonConverter
import me.laotang.carry.core.json.JsonConverter
import me.laotang.carry.di.EmbedJsonConverter
import me.laotang.carry.di.EmbedRepositoryManager
import net.grandcentrix.tray.AppPreferences
import net.grandcentrix.tray.TrayPreferences
import retrofit2.Retrofit
import java.lang.reflect.Type
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideGson(
        @ApplicationContext context: Context,
        configuration: GsonConfiguration?,
        responseDeserializer: JsonDeserializer<BaseResponseBean<*>>
    ): Gson {
        val builder = GsonBuilder()
        configuration?.configGson(context, builder)
        builder
            .registerTypeAdapter(
                BaseResponseBean::class.java,
                responseDeserializer
            ).serializeNulls()
        return builder.create()
    }

    @Singleton
    @Provides
    fun provideAppManager(): AppManager {
        return AppManager.instance
    }

    @Singleton
    @Provides
    @EmbedRepositoryManager
    fun provideRepositoryManager(
        retrofit: Retrofit,
        rxTray: RxTray,
        cacheFactory: Cache.Factory
    ): IRepositoryManager {
        return RepositoryManager(
            retrofit,
            rxTray,
            cacheFactory
        )
    }

    @Singleton
    @Provides
    fun provideRxTray(
        converter: Converter,
        preferences: TrayPreferences
    ): RxTray {
        return RxTray.create(preferences, converter)
    }

    @Singleton
    @Provides
    fun provideTrayConverter(
        jsonConverter: JsonConverter
    ): Converter {
        return object : Converter {
            override fun deserialize(serialized: String, type: Type): Any {
                return jsonConverter.fromJson(serialized, type)
            }

            override fun serialize(value: Any): String {
                return jsonConverter.toJson(value, value.javaClass)
            }

        }
    }

    @Singleton
    @Provides
    fun provideTrayPreferences(
        @ApplicationContext context: Context
    ): TrayPreferences {
        return AppPreferences(context)
    }


    @Singleton
    @Provides
    fun provideImageLoader(
        @ApplicationContext context: Context,
        strategy: ImageLoaderStrategy<ImageLoaderViewTarget<*>>?,
        configuration: ImageLoaderConfiguration?
    ): ImageLoader {
        val imageLoader =
            ImageLoader(strategy)
        configuration?.configImageLoader(context, imageLoader)
        return imageLoader
    }

    @Singleton
    @Provides
    @EmbedJsonConverter
    fun provideJsonConverter(
        gson: Gson
    ): JsonConverter {
        return GsonConverter(gson)
    }
}
