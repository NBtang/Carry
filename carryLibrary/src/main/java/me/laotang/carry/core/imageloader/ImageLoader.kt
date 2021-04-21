package me.laotang.carry.core.imageloader

import android.view.View

class ImageLoader(
    private var mStrategy: ImageLoaderStrategy<ImageLoaderViewTarget<*>>?
) {

    private val imageLoaderInterceptors: HashSet<ImageLoaderInterceptor<ImageLoaderViewTarget<*>>> =
        HashSet()

    fun <T : ImageLoaderViewTarget<*>> loadImage(view: View, viewTarget: T) {
        val interceptors = ArrayList<ImageLoaderInterceptor<ImageLoaderViewTarget<*>>>()
        interceptors.addAll(imageLoaderInterceptors)
        interceptors.add(ImageLoaderReallyInterceptor<T>() as ImageLoaderInterceptor<ImageLoaderViewTarget<*>>)
        this.mStrategy?.let {
            val reallyInterceptorChain =
                ImageLoaderReallyInterceptorChain<T>(
                    it,
                    interceptors
                )
            reallyInterceptorChain.proceed(view, viewTarget)
        }
    }

    fun clear(view: View) {
        this.mStrategy?.clear(view)
    }

    fun setLoadImgStrategy(strategy: ImageLoaderStrategy<ImageLoaderViewTarget<*>>?) {
        this.mStrategy = strategy
    }

    fun getLoadImgStrategy(): ImageLoaderStrategy<ImageLoaderViewTarget<*>>? {
        return mStrategy
    }

    fun addImageLoaderInterceptor(interceptor: ImageLoaderInterceptor<ImageLoaderViewTarget<*>>) {
        imageLoaderInterceptors.add(interceptor)
    }
}