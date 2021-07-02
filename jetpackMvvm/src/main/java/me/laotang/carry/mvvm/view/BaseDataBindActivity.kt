package me.laotang.carry.mvvm.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import me.laotang.carry.core.subscriber.ProgressDialogUtil

abstract class BaseDataBindActivity<T : ViewDataBinding> : AppCompatActivity() {

    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBindingConfig: DataBindingConfig = getDataBindingConfig()
        binding = DataBindingUtil.setContentView(this, layoutId())
        binding.lifecycleOwner = this
        binding.setVariable(
            dataBindingConfig.vmVariableId,
            dataBindingConfig.stateViewModel
        )
        val bindingParams = dataBindingConfig.bindingParams
        for (i in 0 until bindingParams.size()) {
            binding.setVariable(bindingParams.keyAt(i), bindingParams.valueAt(i))
        }
    }

    abstract fun layoutId(): Int

    abstract fun getDataBindingConfig(): DataBindingConfig

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }

    fun showLoading(message: String, cancelable: Boolean = true) {
        ProgressDialogUtil.showLoadingDialog(this, message, cancelable = cancelable)
    }

    fun hideLoading() {
        ProgressDialogUtil.dismissLoadingDialog()
    }
}