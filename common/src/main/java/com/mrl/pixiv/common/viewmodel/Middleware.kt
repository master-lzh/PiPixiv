package com.mrl.pixiv.common.viewmodel

import android.util.Log
import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.util.NetworkExceptionUtil
import com.mrl.pixiv.util.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okio.Closeable
import okio.IOException
import org.koin.core.component.KoinComponent

interface Dispatcher<S : State, A : Action> {
    fun dispatch(action: A)

    fun dispatchError(throwable: Throwable?)

    fun state(): S
}

abstract class Middleware<S : State, A : Action>(
    vararg closeables: Closeable,
) : KoinComponent {
    private lateinit var dispatcher: Dispatcher<S, A>
    private lateinit var scope: CoroutineScope
    private val ioDispatcher = Dispatchers.IO
    private val closeables: Set<Closeable> = setOf(*closeables)

    abstract suspend fun process(state: S, action: A)

    protected fun dispatch(action: A) = dispatcher.dispatch(action)

    protected fun dispatchError(throwable: Throwable?) = dispatcher.dispatchError(throwable)

    protected fun state() = dispatcher.state()

    internal fun setDispatcher(
        dispatcher: Dispatcher<S, A>,
    ) {
        this.dispatcher = dispatcher
    }

    protected fun launchIO(
        onError: (Throwable) -> Unit = { },
        onComplete: () -> Unit = {},
        block: suspend CoroutineScope.() -> Unit
    ) {
        scope.launch(ioDispatcher) {
            try {
                block()
            } catch (e: Throwable) {
                onError(e)
            } finally {
                onComplete()
            }
        }
    }

    protected fun launchNetwork(
        onError: (Throwable) -> Unit = {
            dispatchError(it)
            NetworkExceptionUtil.resolveException(it)
        },
        onComplete: () -> Unit = {},
        block: suspend CoroutineScope.() -> Unit
    ) {
        launchIO(onError, onComplete, block)
    }

    protected fun launchUI(block: suspend () -> Unit) {
        scope.launch {
            block()
        }
    }

    protected fun <T> requestHttpDataWithFlow(
        request: Flow<Rlt<T>>,
        failedCallback: suspend (Throwable) -> Unit = {},
        successCallback: (T) -> Unit,
    ) {
        scope.launch {
            request.flowOn(Dispatchers.Main)
                .catch {
                    failedCallback(it)
                }.collect {
                    when (it) {
                        is Rlt.Success -> successCallback(it.data)
                        is Rlt.Failed -> failedCallback(it.error.exception)
                    }
                }
        }
    }

    internal fun onClear() {
        closeables.forEach { closeable ->
            try {
                closeable.close()
            } catch (ex: IOException) {
                Log.e(TAG, "Exception closing closeable")
                Log.e(TAG, ex.stackTraceToString())
            }
        }
    }

    fun setScope(viewModelScope: CoroutineScope) {
        scope = viewModelScope
    }
}