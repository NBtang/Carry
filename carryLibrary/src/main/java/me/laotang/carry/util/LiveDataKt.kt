package me.laotang.carry.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.arch.core.util.Function

fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    return Transformations.distinctUntilChanged(this)
}

fun <X, Y> LiveData<X>.map(mapFunction: (X) -> Y): LiveData<Y> {
    return Transformations.map(this, Function<X, Y> {
        return@Function mapFunction(it)
    })
}

fun <X, Y> LiveData<X>.switchMap(switchMapFunction: (X) -> LiveData<Y>): LiveData<Y> {
    return Transformations.switchMap(this, Function<X, LiveData<Y>> {
        return@Function switchMapFunction(it)
    })
}

