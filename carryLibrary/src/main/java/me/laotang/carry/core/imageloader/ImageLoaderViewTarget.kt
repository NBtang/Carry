package me.laotang.carry.core.imageloader

import android.graphics.drawable.Drawable
import android.net.Uri
import java.io.File

open class ImageLoaderViewTarget<T>(val scr: T) {
    var placeholder: Int = 0
    var errorPic: Int = 0
    var isCenterCrop: Boolean = false
    var isCircle: Boolean = false
    var imageRadius: Int = 0
    var targetWidth: Int = 0
    var targetHeight: Int = 0

    //0对应DiskCacheStrategy.all,1对应DiskCacheStrategy.NONE,2对应DiskCacheStrategy.SOURCE,3对应DiskCacheStrategy.RESULT
    var cacheStrategy: Int = 0

    var crossFade: Boolean = false

    fun isImageRadius(): Boolean {
        return imageRadius > 0
    }

    open fun onLoadStarted(placeholder: Drawable?) {

    }

    open fun onLoadFailed(errorDrawable: Drawable?) {

    }

    open fun onResourceReady(resource: Drawable) {

    }
}

open class UrlImageLoaderViewTarget(url: String) : ImageLoaderViewTarget<String>(url) {
    open fun copy(block: UrlImageLoaderViewTarget.() -> Unit): UrlImageLoaderViewTarget {
        val target =
            UrlImageLoaderViewTarget(scr)
        target.block()
        return target
    }
}

open class FileImageLoaderViewTarget(file: File) : ImageLoaderViewTarget<File>(file) {
    open fun copy(block: FileImageLoaderViewTarget.() -> Unit): FileImageLoaderViewTarget {
        val target =
            FileImageLoaderViewTarget(
                scr
            )
        target.block()
        return target
    }
}

open class UriImageLoaderViewTarget(uri: Uri) : ImageLoaderViewTarget<Uri>(uri) {
    open fun copy(block: UriImageLoaderViewTarget.() -> Unit): UriImageLoaderViewTarget {
        val target =
            UriImageLoaderViewTarget(scr)
        target.block()
        return target
    }
}

open class ResImageLoaderViewTarget(resId: Int) : ImageLoaderViewTarget<Int>(resId) {
    open fun copy(block: ResImageLoaderViewTarget.() -> Unit): ResImageLoaderViewTarget {
        val target =
            ResImageLoaderViewTarget(scr)
        target.block()
        return target
    }
}

inline fun <T> ImageLoaderViewTarget<T>.config(block: ImageLoaderViewTarget<T>.() -> Unit): ImageLoaderViewTarget<T> {
    this.block()
    return this
}