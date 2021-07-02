package me.laotang.carry.demo.ui.view

import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.functions.Consumer
import me.laotang.carry.core.json.JsonConverter
import me.laotang.carry.demo.BR
import me.laotang.carry.demo.R
import me.laotang.carry.demo.databinding.ActivityMainBinding
import me.laotang.carry.demo.ui.state.MainViewModel
import me.laotang.carry.mvvm.view.BaseDataBindActivity
import me.laotang.carry.mvvm.view.DataBindingConfig
import me.laotang.carry.util.toasty
import javax.inject.Inject

/**
 * 通过dataBinding实现UI的数据驱动
 * viewModel作为state使用，view层的数据要与viewModel中保持一致，除了某些数据比如滑动到了某个位置等状态
 * 可以通过viewModel中的数据，恢复view层的UI
 */
@AndroidEntryPoint
class MainActivity : BaseDataBindActivity<ActivityMainBinding>() {

    @Inject
    lateinit var jsonConverter: JsonConverter

    private val state by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Request响应view层的生命周期，自动取消任务
        lifecycle.addObserver(state.userInfoRequest)

        //监听loading显示事件
        state.showLoadingLiveData.observe(this, {
            if (it) {
                showLoading("加载中...")
            } else {
                hideLoading()
            }
        })
    }

    override fun layoutId(): Int = R.layout.activity_main

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(vmVariableId = BR.vm, stateViewModel = state)
            .addBindingParam(BR.listener, ListenerHandler(state))
    }

    /**
     * MainActivity的UI响应事件（如点击），单独拎出来，通过dataBinding实现绑定，交互的数据由viewModel层提供
     * MainActivity处理其他和model层不交互的UI事件
     * 进一步保证view层的UI显示由viewModel中的数据来驱动
     */
    class ListenerHandler(state: MainViewModel) {
        val userInfoRequestConsumer: Consumer<Unit> by lazy {
            Consumer<Unit> {
                val lastIdQueried = state.lastIdQueried.toIntOrNull()
                if (lastIdQueried == null) {
                    "Id不能为空".toasty()
                    return@Consumer
                }
                state.userInfoRequest.getUsers(lastIdQueried)
            }
        }

    }
}