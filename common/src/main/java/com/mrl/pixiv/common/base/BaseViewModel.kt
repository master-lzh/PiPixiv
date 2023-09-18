package com.mrl.pixiv.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrl.pixiv.common.data.DispatcherEnum
import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.util.NetworkExceptionUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

open class BaseViewModel : ViewModel(), KoinComponent {
    protected val ioDispatcher: CoroutineDispatcher by inject(named(DispatcherEnum.IO))
    private val _exception = MutableSharedFlow<Throwable?>()
    val exception = _exception.asSharedFlow()
    protected fun launchIO(
        onError: (Throwable) -> Unit = { viewModelScope.launch { _exception.emit(it) } },
        onComplete: () -> Unit = {},
        block: suspend () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
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
            viewModelScope.launch {
                _exception.emit(it)
            }
            NetworkExceptionUtil.resolveException(it)
        },
        onComplete: () -> Unit = {},
        block: suspend () -> Unit
    ) {
        launchIO(onError, onComplete, block)
    }

    protected fun launchUI(block: suspend () -> Unit) {
        viewModelScope.launch {
            block()
        }
    }

    protected fun <T> requestHttpDataWithFlow(
        request: Flow<Rlt<T?>>,
        failedCallback: suspend (Throwable) -> Unit = {},
        successCallback: (T?) -> Unit,
    ) {
        viewModelScope.launch {
            request.flowOn(Dispatchers.Default)
                .catch {
                    failedCallback(it)
                }.flowOn(Dispatchers.Main)
                .collect {
                    when (it) {
                        is Rlt.Success -> successCallback(it.data)
                        is Rlt.Failed -> failedCallback(it.error.exception)
                    }
                }
        }
    }
}