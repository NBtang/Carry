package me.laotang.carry.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import me.laotang.carry.core.IRepositoryManager
import me.laotang.carry.core.json.JsonConverter
import me.laotang.carry.core.obtainPreferences
import me.laotang.carry.core.preference
import me.laotang.carry.core.preferenceLiveData
import me.laotang.carry.demo.databinding.ActivityMainBinding
import me.laotang.carry.util.clickObserver
import timber.log.Timber
import javax.inject.Inject

/**
 * 通过dataBinding实现UI的数据驱动
 * viewModel作为Store使用，view层的数据要与viewModel中保持一致，除了某些数据比如滑动到了某个位置等状态
 * 可以通过viewModel中的数据，恢复view层的UI
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var jsonConverter: JsonConverter

    @Inject
    lateinit var repositoryManager: IRepositoryManager

    private lateinit var binding: ActivityMainBinding

    private var isFirst by preference("isFirst",false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        preferenceLiveData("isFirst",false).observe(this,{
            Timber.d("isFirst asLiveData:$it")
        })

        binding.tvContent.clickObserver {
            isFirst = true
        }

        Timber.d("isFirst :$isFirst")
    }

    override fun onResume() {
        super.onResume()
        isFirst = true
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }
}