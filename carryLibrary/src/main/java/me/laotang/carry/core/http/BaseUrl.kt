package me.laotang.carry.core.http

import okhttp3.HttpUrl

interface BaseUrl {
    fun url(): HttpUrl?
}