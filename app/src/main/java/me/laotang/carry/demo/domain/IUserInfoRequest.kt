package me.laotang.carry.demo.domain

import androidx.lifecycle.LifecycleObserver

/**
 * UserInfoRequest 提供的数据请求能力
 */
interface IUserInfoRequest : LifecycleObserver {
    fun getUsers(lastIdQueried: Int = 0, loading: Boolean = true)
}