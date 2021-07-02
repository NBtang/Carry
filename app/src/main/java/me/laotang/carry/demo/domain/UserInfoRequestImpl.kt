package me.laotang.carry.demo.domain

import androidx.lifecycle.*
import me.laotang.carry.demo.model.entity.User
import me.laotang.carry.demo.model.repository.DataRepository
import me.laotang.carry.mvvm.domain.BaseRequest
import javax.inject.Inject

class UserInfoRequestImpl @Inject constructor(private val dataRepository: DataRepository) :
    BaseRequest(), IUserInfoRequest {

    private val mUsersLiveData: MutableLiveData<List<User>> by lazy {
        MutableLiveData()
    }

    val usersLiveData: LiveData<List<User>>
        get() = mUsersLiveData.distinctUntilChanged()

    private val mLoadingState: UnPeekLiveData<Boolean> by lazy {
        UnPeekLiveData()
    }

    val loadingState: LiveData<Boolean>
        get() = mLoadingState

    override fun getUsers(lastIdQueried: Int, loading: Boolean) {
        launch({
            if (loading) {
                mLoadingState.setValue(true)
            }
            val users = dataRepository.getUsers(lastIdQueried)
            if (loading) {
                mLoadingState.setValue(false)
            }
            mUsersLiveData.setValue(users)
        }, {
            if (loading) {
                mLoadingState.setValue(false)
            }
            mUsersLiveData.setValue(listOf())
        })
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mLoadingState.setValue(false)
        super.onDestroy(owner)
    }
}