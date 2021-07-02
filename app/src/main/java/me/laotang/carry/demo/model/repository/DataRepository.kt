package me.laotang.carry.demo.model.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.laotang.carry.core.IRepositoryManager
import me.laotang.carry.demo.model.api.ApiService
import me.laotang.carry.demo.model.entity.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(private val repositoryManager: IRepositoryManager) {

    private val apiService: ApiService by lazy {
        repositoryManager.obtainRetrofitService(ApiService::class.java)
    }

    suspend fun getUsers(lastIdQueried: Int = 0): List<User> {
        return withContext(Dispatchers.IO) {
            apiService.getUsers(lastIdQueried, 10)
        }
    }
}