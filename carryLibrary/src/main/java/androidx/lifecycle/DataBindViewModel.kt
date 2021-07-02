package androidx.lifecycle

import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.io.Closeable

/**
 * 通过CompositeDisposable管理生命周期
 */
private const val DISPOSABLE_KEY = "androidx.lifecycle.ViewModel.Disposable.DISPOSABLE_KEY"

fun ViewModel.autoDisposable(vararg disposable: Disposable) {
    var delegate: CompositeDisposableDelegate? = this.getTag(DISPOSABLE_KEY)
    if (delegate == null) {
        delegate = CompositeDisposableDelegate()
        this.setTagIfAbsent(DISPOSABLE_KEY, delegate)
    }
    delegate.add(*disposable)
}

internal class CompositeDisposableDelegate : Closeable {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    internal fun add(vararg disposable: Disposable) {
        compositeDisposable.addAll(*disposable)
    }

    override fun close() {
        compositeDisposable.dispose()
    }
}

/**
 * 通过takeUntil管理生命周期
 */
private const val RX_SCOPE_KEY = "androidx.lifecycle.ViewModel.OnClear.RX_SCOPE_KEY"

val ViewModel.viewModelRxScope: BindLifecycleHolder
    get() {
        var holder: BindLifecycleHolder? = this.getTag(RX_SCOPE_KEY)
        if (holder == null) {
            holder = BindLifecycleHolder()
            holder = this.setTagIfAbsent(RX_SCOPE_KEY, holder)
        }
        return holder!!
    }

fun <T> Observable<T>.bindRxScope(viewModelLifecycle: BindLifecycleHolder): Observable<T> {
    return this.compose(LifecycleTransformer(viewModelLifecycle.lifecycleSubject))
}

fun <T> Flowable<T>.bindRxScope(viewModelLifecycle: BindLifecycleHolder): Flowable<T> {
    return this.compose(LifecycleTransformer(viewModelLifecycle.lifecycleSubject))
}

fun <T> Single<T>.bindRxScope(viewModelLifecycle: BindLifecycleHolder): Single<T> {
    return this.compose(LifecycleTransformer(viewModelLifecycle.lifecycleSubject))
}

fun <T> Maybe<T>.bindRxScope(viewModelLifecycle: BindLifecycleHolder): Maybe<T> {
    return this.compose(LifecycleTransformer(viewModelLifecycle.lifecycleSubject))
}

fun Completable.bindRxScope(viewModelLifecycle: BindLifecycleHolder): Completable {
    return this.compose(LifecycleTransformer<Any>(viewModelLifecycle.lifecycleSubject))
}

class BindLifecycleHolder : Closeable {

    private val beSubject: BehaviorSubject<Unit> = BehaviorSubject.create()

    internal val lifecycleSubject: Observable<Unit>
        get() = beSubject

    override fun close() {
        beSubject.onNext(Unit)
    }

}


/**
 * 通过CompositeDisposable管理生命周期
 */
private const val LIFECYCLE_OWNER_KEY =
    "androidx.lifecycle.ViewModel.LifecycleOwner.LIFECYCLE_OWNER_KEY"

fun ViewModel.setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
    var delegate: LifecycleRegistryDelegate? = this.getTag(LIFECYCLE_OWNER_KEY)
    if (delegate == null) {
        delegate = LifecycleRegistryDelegate()
        this.setTagIfAbsent(LIFECYCLE_OWNER_KEY, delegate)
    }
    lifecycleOwner.lifecycle.addObserver(delegate)
    if (this is LifecycleObserver) {
        delegate.lifecycle.addObserver(this)
    }
}

val ViewModel.lifecycleOwner: LifecycleOwner?
    get() {
        return getTag<LifecycleRegistryDelegate>(LIFECYCLE_OWNER_KEY)
    }

internal class LifecycleRegistryDelegate :
    LifecycleOwner, FullLifecycleObserver {

    private lateinit var mLifecycleRegistry: LifecycleRegistry

    init {
        initLifecycle()
    }

    private fun initLifecycle() {
        mLifecycleRegistry = LifecycleRegistry(this)
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

    override fun onCreate(owner: LifecycleOwner?) {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onStart(owner: LifecycleOwner?) {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onResume(owner: LifecycleOwner?) {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onPause(owner: LifecycleOwner?) {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun onStop(owner: LifecycleOwner?) {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroy(owner: LifecycleOwner?) {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        owner?.lifecycle?.removeObserver(this)
    }
}


