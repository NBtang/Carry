package me.laotang.carry.core

import com.laotang.quickdev.rxtray.Preference
import java.lang.reflect.Type

interface IRepositoryManager {
    /**
     * 根据传入的 Class 获取对应的 Retrofit service
     *
     * @param service
     * @param <T>
     * @return
    </T> */
    fun <T> obtainRetrofitService(service: Class<T>): T

    /**
     * 获取Preferences，管理本地偏好设置缓存
     */
    fun <T> obtainPreferencesService(key: String, defaultValue: T, type: Type): Preference<T>

    /**
     * 清理所有缓存
     */
    fun clearAllCache()
}