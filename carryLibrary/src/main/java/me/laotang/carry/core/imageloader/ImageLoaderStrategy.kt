package me.laotang.carry.core.imageloader

import android.view.View

interface ImageLoaderStrategy<T : ImageLoaderViewTarget<*>> {
    fun load(view: View, viewTarget: T)
    fun clear(view: View)
}

