package me.laotang.carry.core.http.response

class ErrorReport(message: String?, val code: Int = 0) : Throwable(message) {
}