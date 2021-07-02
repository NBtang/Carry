package androidx.lifecycle

import android.app.Application
import android.content.Context
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment

/**
 * 作用域为全局的ViewModelStoreOwner
 */
class ViewModelStoreOwnerHolder : ViewModelStoreOwner {

    private var viewModelStore: ViewModelStore? = null

    override fun getViewModelStore(): ViewModelStore {
        if (viewModelStore == null) {
            viewModelStore = ViewModelStore()
        }
        return viewModelStore!!
    }

    companion object {
        @JvmStatic
        private val INSTANCE: ViewModelStoreOwnerHolder by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ViewModelStoreOwnerHolder()
        }

        fun getInstance(): ViewModelStoreOwnerHolder {
            return INSTANCE
        }
    }
}


@MainThread
inline fun <reified VM : ViewModel> Context.shareViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        ViewModelProvider.AndroidViewModelFactory.getInstance(applicationContext as Application)
    }

    return ViewModelLazy(
        VM::class,
        { ViewModelStoreOwnerHolder.getInstance().viewModelStore },
        factoryPromise
    )
}


@MainThread
inline fun <reified VM : ViewModel> Fragment.shareViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        ViewModelProvider.AndroidViewModelFactory.getInstance(requireContext().applicationContext as Application)
    }

    return ViewModelLazy(
        VM::class,
        { ViewModelStoreOwnerHolder.getInstance().viewModelStore },
        factoryPromise
    )
}

