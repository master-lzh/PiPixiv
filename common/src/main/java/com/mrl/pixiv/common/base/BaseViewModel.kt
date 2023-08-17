package com.mrl.pixiv.common.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrl.pixiv.common.data.BaseUiState
import com.mrl.pixiv.common.data.DispatcherEnum
import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.common.data.UiIntent
import com.mrl.pixiv.common.data.failed
import com.mrl.pixiv.util.NetworkExceptionUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

abstract class BaseViewModel<S : BaseUiState, I : UiIntent> : ViewModel(), KoinComponent {
    protected val TAG = this::class.java.simpleName

    private val _uiStateFlow by lazy { MutableStateFlow(initUiState()) }
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _userIntent = MutableSharedFlow<I>()
    protected val userIntent = _userIntent.asSharedFlow()

    private val _exception = MutableSharedFlow<Throwable?>()
    val exception = _exception.asSharedFlow()

    protected val ioDispatcher: CoroutineDispatcher by inject(named(DispatcherEnum.IO))

    init {
        viewModelScope.launch {
            userIntent.distinctUntilChanged { old, new ->
                if (new.forceHandle) {
                    false
                } else {
                    old == new
                }
            }.collect {
                handleUserIntent(it)
            }
        }
    }

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


    protected abstract fun handleUserIntent(intent: I)
    abstract fun initUiState(): S

    protected fun updateUiState(block: S.() -> S) {
        viewModelScope.launch {
            Log.d(TAG, "updateUiState: $_uiStateFlow")
            _uiStateFlow.update { it.block() }
        }
    }

    fun dispatch(intent: I) {
        viewModelScope.launch {
            _userIntent.emit(intent)
        }
    }

    protected fun <T> requestHttpDataWithFlow(
        showLoading: Boolean = true,
        request: Flow<Rlt<T?>>,
        failedCallback: suspend (Throwable) -> Unit = {
            updateUiState { apply { failure = it } }
        },
        successCallback: (T?) -> Unit,
    ) {
        viewModelScope.launch {
            request.onStart {
                if (showLoading) {
                    updateUiState { apply { loading = true } }
                }
            }.flowOn(Dispatchers.Default)
                .catch {
                    Log.e(TAG, "requestHttpDataWithFlow: ${it.printStackTrace()}")
                    failedCallback(it)
                    updateUiState { apply { failed(it) } }
                }.onCompletion {
                    updateUiState { apply { loading = false } }
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