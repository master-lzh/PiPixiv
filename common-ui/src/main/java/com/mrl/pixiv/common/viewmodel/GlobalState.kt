package com.mrl.pixiv.common.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrl.pixiv.common.coroutine.launchNetwork
import com.mrl.pixiv.common.data.Rlt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update

abstract class GlobalState<S>(
    initialSate: S,
) {
    private val _state = MutableStateFlow(initialSate)

    val state: S
        @Composable
        get() = _state.collectAsStateWithLifecycle().value

    protected fun updateState(newState: (S) -> S) = _state.update(newState)


    protected fun <T> requestHttpDataWithFlow(
        request: Flow<Rlt<T>>,
        failedCallback: suspend (Throwable) -> Unit = {},
        successCallback: (T) -> Unit,
    ) {
        launchNetwork {
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
}