package me.laotang.carry.core.subscriber

import me.laotang.carry.AppManager
import me.laotang.carry.di.RxProgressConfiguration
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ApplicationComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandlerFactory

abstract class RxSubscriber<T>(
    msg: String = "",
    showProgress: Boolean = true,
    cancelable: Boolean = true
) : DisposableObserver<T>() {

    @EntryPoint
    @InstallIn(ApplicationComponent::class)
    interface RxSubscriberEntryPoint {
        fun rxErrorHandler(): RxErrorHandler
        fun rxProgressConfiguration(): RxProgressConfiguration
    }

    private var progressObservable: RxProgressObservable? = null
    private var cancelDisposable: Disposable? = null

    private val mHandlerFactory: ErrorHandlerFactory

    init {
        val rxSubscriberEntryPoint = EntryPointAccessors.fromApplication(AppManager.instance.getApplicationContext(),
            RxSubscriberEntryPoint::class.java)
        val rxProgressConfiguration = rxSubscriberEntryPoint.rxProgressConfiguration()
        mHandlerFactory = rxSubscriberEntryPoint.rxErrorHandler().handlerFactory
        if (showProgress && msg.isNotEmpty()) {
            progressObservable = rxProgressConfiguration.provideRxProgressObservable(msg, cancelable)
        }
    }

    override fun onStart() {
        super.onStart()
        val instance = this
        val activity = AppManager.instance.getTopActivity()
        if (activity != null) {
            val cancelObservable = progressObservable?.showProgress(activity)
            cancelDisposable = cancelObservable?.let { observable ->
                observable.observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (it) {
                            instance.dispose()
                        }
                    }
            }
        }
    }

    override fun onNext(t: T) {
        dismissLoadingDialog()
        _onNext(t)
    }

    override fun onComplete() {
        dismissLoadingDialog()
    }

    final override fun onError(e: Throwable) {
        dismissLoadingDialog()
        e.printStackTrace()
        _onError(e)
    }

    open fun _onError(e: Throwable) {
        //???????????????????????????????????????????????????,????????? _onError(Throwable) ?????? super._onError(e); ??????
        //??????????????????????????????????????????,???????????????????????????,????????? _onError(Throwable) ?????? super._onError(e); ???????????????????????????
        mHandlerFactory.handleError(e)
    }

    private fun dismissLoadingDialog() {
        cancelDisposable?.apply {
            if (!isDisposed) {
                dispose()
            }
        }
        progressObservable?.apply {
            if (isShowing()) {
                dismiss()
            }
        }
    }

    abstract fun _onNext(t: T)

}