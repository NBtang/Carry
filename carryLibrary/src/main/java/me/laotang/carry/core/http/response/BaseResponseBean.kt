package me.laotang.carry.core.http.response

class BaseResponseBean<T> : ResponseBean<T>() {
    var code: Int = 0
    var message: String = ""
    var success: Boolean = false
}