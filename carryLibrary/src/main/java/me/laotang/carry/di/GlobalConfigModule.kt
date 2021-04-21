package me.laotang.carry.di

import android.content.Context
import android.text.TextUtils
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener
import me.laotang.carry.ManifestDynamicAdapter
import me.laotang.carry.core.cache.Cache
import me.laotang.carry.core.cache.CacheFactory
import me.laotang.carry.core.http.BaseUrl
import me.laotang.carry.core.http.HttpLoggingInterceptor
import me.laotang.carry.core.http.response.BaseResponseBean
import me.laotang.carry.core.imageloader.ImageLoader
import me.laotang.carry.core.imageloader.ImageLoaderStrategy
import me.laotang.carry.core.imageloader.ImageLoaderViewTarget
import me.laotang.carry.core.subscriber.RxProgressObservable
import me.laotang.carry.core.subscriber.RxProgressObservableImpl
import me.laotang.carry.util.getDefaultCacheFile
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.util.concurrent.ExecutorService


class GlobalConfigModule private constructor(builder: Builder) {

    private var mApiUrl: HttpUrl? = null
    private var mBaseUrl: BaseUrl? = null
    private var mCacheFile: File? = null
    private var mCacheFactory: Cache.Factory? = null
    private var mRetrofitConfiguration: RetrofitConfiguration? = null
    private var mOkHttpConfiguration: OkHttpConfiguration? = null
    private var mErrorListener: ResponseErrorListener? = null
    private var mGsonConfiguration: GsonConfiguration? = null
    private var mRxProgressConfiguration: RxProgressConfiguration? = null
    private var mLoaderStrategy: ImageLoaderStrategy<ImageLoaderViewTarget<*>>? = null
    private var mImageLoaderConfiguration: ImageLoaderConfiguration? = null
    private var mResponseDeserializer: JsonDeserializer<BaseResponseBean<*>>? = null
    private var mActivityConfigAdapter: ManifestDynamicAdapter.ActivityConfigAdapter? = null
    private var mEnableHttpLogging: Boolean = false

    init {
        this.mApiUrl = builder.apiUrl
        this.mBaseUrl = builder.url
        this.mCacheFile = builder.cacheFile
        this.mCacheFactory = builder.cacheFactory
        this.mRetrofitConfiguration = builder.retrofitConfiguration
        this.mOkHttpConfiguration = builder.okHttpConfiguration
        this.mErrorListener = builder.responseErrorListener
        this.mGsonConfiguration = builder.gsonConfiguration
        this.mRxProgressConfiguration = builder.rxProgressConfiguration
        this.mLoaderStrategy = builder.loaderStrategy
        this.mImageLoaderConfiguration = builder.imageLoaderConfiguration
        this.mResponseDeserializer = builder.responseDeserializer
        this.mActivityConfigAdapter = builder.activityConfigAdapter
        this.mEnableHttpLogging = builder.enableHttpLogging
    }

    fun provideBaseUrl(): HttpUrl {
        val url = mBaseUrl?.url()
        return url ?: (mApiUrl ?: throw AssertionError("url empty"))
    }

    fun provideCacheFile(context: Context): File {
        return mCacheFile ?: context.getDefaultCacheFile()
    }

    fun provideCacheFactory(context: Context): Cache.Factory {
        return mCacheFactory ?: CacheFactory(
            context
        )
    }

    fun provideRetrofitConfiguration(): RetrofitConfiguration? {
        return mRetrofitConfiguration
    }

    fun provideOkHttpConfiguration(): OkHttpConfiguration? {
        return mOkHttpConfiguration
    }

    fun provideResponseErrorListener(): ResponseErrorListener {
        return mErrorListener ?: ResponseErrorListener.EMPTY
    }

    fun provideGsonConfiguration(): GsonConfiguration? {
        return mGsonConfiguration
    }

    fun provideRxProgressConfiguration(): RxProgressConfiguration {
        return mRxProgressConfiguration ?: (object : RxProgressConfiguration {
            override fun provideRxProgressObservable(
                msg: String,
                cancelable: Boolean
            ): RxProgressObservable {
                return RxProgressObservableImpl(
                    msg,
                    cancelable
                )
            }
        })
    }

    fun provideImageLoaderStrategy(): ImageLoaderStrategy<ImageLoaderViewTarget<*>>? {
        var glideImageLoaderStrategy: ImageLoaderStrategy<ImageLoaderViewTarget<*>>? = null
        try {
            val clazz = Class.forName("me.laotang.carry.glide.GlideImageLoaderStrategy")
            glideImageLoaderStrategy =
                clazz.newInstance() as ImageLoaderStrategy<ImageLoaderViewTarget<*>>
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mLoaderStrategy ?: glideImageLoaderStrategy
    }

    fun provideImageLoaderConfiguration(): ImageLoaderConfiguration? {
        return mImageLoaderConfiguration
    }

    fun provideResponseDeserializer(): JsonDeserializer<BaseResponseBean<*>>? {
        return mResponseDeserializer
    }

    fun provideActivityConfigAdapter(): ManifestDynamicAdapter.ActivityConfigAdapter? {
        return mActivityConfigAdapter
    }

    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor? {
        return if (mEnableHttpLogging) HttpLoggingInterceptor { _, message ->
            Timber.tag("OkHttp")
            Timber.d(message)
        }.setLevel(
            HttpLoggingInterceptor.Level.BODY
        ) else null
    }

    class Builder {
        internal var apiUrl: HttpUrl? = null
        internal var url: BaseUrl? = null
        internal var cacheFile: File? = null
        internal var cacheFactory: Cache.Factory? = null
        internal var retrofitConfiguration: RetrofitConfiguration? = null
        internal var okHttpConfiguration: OkHttpConfiguration? = null
        internal var responseErrorListener: ResponseErrorListener? = null
        internal var executorService: ExecutorService? = null
        internal var gsonConfiguration: GsonConfiguration? = null
        internal var rxProgressConfiguration: RxProgressConfiguration? = null
        internal var loaderStrategy: ImageLoaderStrategy<ImageLoaderViewTarget<*>>? = null
        internal var imageLoaderConfiguration: ImageLoaderConfiguration? = null
        internal var responseDeserializer: JsonDeserializer<BaseResponseBean<*>>? = null
        internal var activityConfigAdapter: ManifestDynamicAdapter.ActivityConfigAdapter? = null
        internal var enableHttpLogging: Boolean = false

        fun baseUrl(baseUrl: String): Builder {//基础url
            if (TextUtils.isEmpty(baseUrl)) {
                throw NullPointerException("BaseUrl can not be empty")
            }
            this.apiUrl = baseUrl.toHttpUrlOrNull()
            return this
        }

        fun baseUrl(baseUrl: BaseUrl): Builder {
            this.url = baseUrl
            return this
        }

        fun cacheFile(cacheFile: File): Builder {
            this.cacheFile = cacheFile
            return this
        }

        fun cacheFactory(cacheFactory: Cache.Factory): Builder {
            this.cacheFactory = cacheFactory
            return this
        }

        fun retrofitConfiguration(retrofitConfiguration: RetrofitConfiguration): Builder {
            if (this.retrofitConfiguration == null) {
                this.retrofitConfiguration = RetrofitConfigurationImpl()
            }
            (this.retrofitConfiguration as RetrofitConfigurationImpl)
                .addRetrofitConfiguration(retrofitConfiguration)
            return this
        }

        fun okHttpConfiguration(okHttpConfiguration: OkHttpConfiguration): Builder {
            if (this.okHttpConfiguration == null) {
                this.okHttpConfiguration = OkHttpConfigurationImpl()
            }
            (this.okHttpConfiguration as OkHttpConfigurationImpl)
                .addOkHttpConfiguration(okHttpConfiguration)
            return this
        }

        fun responseErrorListener(listener: ResponseErrorListener): Builder {//处理所有RxJava的onError逻辑
            this.responseErrorListener = listener
            return this
        }

        fun executorService(executorService: ExecutorService): Builder {
            this.executorService = executorService
            return this
        }

        fun gsonConfiguration(gsonConfiguration: GsonConfiguration): Builder {
            if (this.gsonConfiguration == null) {
                this.gsonConfiguration = GsonConfigurationImpl()
            }
            (this.gsonConfiguration as GsonConfigurationImpl)
                .addGsonConfiguration(gsonConfiguration)
            return this
        }

        fun rxProgressConfiguration(rxProgressConfiguration: RxProgressConfiguration): Builder {
            this.rxProgressConfiguration = rxProgressConfiguration
            return this
        }

        fun imageLoaderStrategy(loaderStrategy: ImageLoaderStrategy<ImageLoaderViewTarget<*>>): Builder {//用来请求网络图片
            this.loaderStrategy = loaderStrategy
            return this
        }

        fun imageLoaderConfiguration(imageLoaderConfiguration: ImageLoaderConfiguration): Builder {
            this.imageLoaderConfiguration = imageLoaderConfiguration
            return this
        }

        fun responseDeserializer(responseDeserializer: JsonDeserializer<BaseResponseBean<*>>): Builder {
            this.responseDeserializer = responseDeserializer
            return this
        }

        fun activityConfigAdapter(activityConfigAdapter: ManifestDynamicAdapter.ActivityConfigAdapter): Builder {
            this.activityConfigAdapter = activityConfigAdapter
            return this
        }

        fun enableHttpLogging(enable: Boolean = true): Builder {
            this.enableHttpLogging = enable
            return this
        }

        fun build(): GlobalConfigModule {
            return GlobalConfigModule(this)
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}

interface GsonConfiguration {
    fun configGson(context: Context, gsonBuilder: GsonBuilder)
}

interface RxProgressConfiguration {
    fun provideRxProgressObservable(msg: String, cancelable: Boolean): RxProgressObservable
}

interface RetrofitConfiguration {
    fun configRetrofit(context: Context, builder: Retrofit.Builder)
}

interface OkHttpConfiguration {
    fun configOkHttp(context: Context, builder: OkHttpClient.Builder)
}

interface ImageLoaderConfiguration {
    fun configImageLoader(context: Context, imageLoader: ImageLoader)
}

class OkHttpConfigurationImpl : OkHttpConfiguration {
    private var mOkHttpConfigurations: MutableList<OkHttpConfiguration>? = null

    fun addOkHttpConfiguration(okHttpConfiguration: OkHttpConfiguration) {
        if (mOkHttpConfigurations == null) {
            mOkHttpConfigurations = mutableListOf()
        }
        mOkHttpConfigurations!!.add(okHttpConfiguration)
    }

    override fun configOkHttp(context: Context, builder: OkHttpClient.Builder) {
        mOkHttpConfigurations?.forEach {
            it.configOkHttp(context, builder)
        }
    }
}


class RetrofitConfigurationImpl : RetrofitConfiguration {
    private var mRetrofitConfigurations: MutableList<RetrofitConfiguration>? = null

    fun addRetrofitConfiguration(retrofitConfiguration: RetrofitConfiguration) {
        if (mRetrofitConfigurations == null) {
            mRetrofitConfigurations = mutableListOf()
        }
        mRetrofitConfigurations!!.add(retrofitConfiguration)
    }

    override fun configRetrofit(context: Context, builder: Retrofit.Builder) {
        mRetrofitConfigurations?.forEach {
            it.configRetrofit(context, builder)
        }
    }
}


class GsonConfigurationImpl : GsonConfiguration {
    private var mGsonConfigurations: MutableList<GsonConfiguration>? = null

    fun addGsonConfiguration(gsonConfiguration: GsonConfiguration) {
        if (mGsonConfigurations == null) {
            mGsonConfigurations = mutableListOf()
        }
        mGsonConfigurations!!.add(gsonConfiguration)
    }

    override fun configGson(context: Context, gsonBuilder: GsonBuilder) {
        mGsonConfigurations?.forEach {
            it.configGson(context, gsonBuilder)
        }
    }
}