package me.laotang.carry.demo.ui.state

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import io.reactivex.functions.Consumer
import me.laotang.carry.demo.domain.IUserInfoRequest
import me.laotang.carry.demo.domain.UserInfoRequestImpl
import me.laotang.carry.demo.model.entity.User
import me.laotang.carry.mvvm.vm.BaseViewModel

/**
 * 网络请求由Request维护，方便在多个viewModel中复用，一个viewModel也可以拥有多个Request
 * UI页面相关数据由viewModel维护，如（EditText中的内容、CheckBox的选中状态，网络请求返回的数据等）
 * viewModel尽量作为state使用，不涉及逻辑
 */
class MainViewModel @ViewModelInject constructor(private val mUserInfoRequestImpl: UserInfoRequestImpl) :
    BaseViewModel() {

    //MainActivity页面的state，每一个状态都对应相应的UI
    private val mData: MediatorLiveData<MainViewData> by lazy {
        val livedData = MediatorLiveData<MainViewData>()
        livedData.value = MainViewData()
        return@lazy livedData
    }

    //以接口的形式提供，避免view层接触数据，保证viewModel为唯一可信数据源
    val userInfoRequest: IUserInfoRequest
        get() = mUserInfoRequestImpl

    //通过map返回指定的数据类型
    val usersLiveData: LiveData<List<User>>
        get() = mData.map { it.users }.distinctUntilChanged()

    val showLoadingLiveData: LiveData<Boolean>
        get() = mData.map { it.showLoading }.distinctUntilChanged()

    val lastIdQueried: String
        get() = mData.value?.lastIdQueried ?: ""

    //通过dataBinding实现和view层的editText的textChange事件的绑定，在viewModel实时保存
    //单项绑定
    val lastIdQueriedChanges: Consumer<CharSequence> by lazy {
        Consumer<CharSequence> {
            val lastIdQueried = it.toString()
            val oldData = mData.value ?: MainViewData()
            mData.value = oldData.copy(lastIdQueried = lastIdQueried)
        }
    }

    init {
        //订阅Request请求后返回的数据
        mData.addSource(mUserInfoRequestImpl.usersLiveData) {
            val oldData = mData.value ?: MainViewData()
            mData.value = oldData.copy(users = it)
        }
        //是否显示loading框
        mData.addSource(mUserInfoRequestImpl.loadingState) {
            val oldData = mData.value ?: MainViewData()
            mData.value = oldData.copy(showLoading = it)
        }
    }
}

/**
 * view层所需要的全部数据
 */
data class MainViewData(
    val users: List<User> = listOf(),
    val lastIdQueried: String = "0",
    val showLoading: Boolean = false,
)