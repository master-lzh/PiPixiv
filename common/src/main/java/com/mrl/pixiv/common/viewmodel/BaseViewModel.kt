package com.mrl.pixiv.common.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.mrl.pixiv.util.TAG
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val BufferSize = 64

abstract class BaseViewModel<S : State, A : Action>(
    private val reducer: Reducer<S, A>,
    private val middlewares: List<Middleware<S, A>> = emptyList(),
    initialState: S,
) : ViewModel(), Dispatcher<S, A> {
    private val _exception = MutableSharedFlow<Throwable?>()
    val exception = _exception.asSharedFlow()

    private data class ActionImpl<S : State, A : Action>(
        val state: S,
        val action: A,
    )

    private val actions = MutableSharedFlow<ActionImpl<S, A>>(extraBufferCapacity = BufferSize)

    private val _state =  MutableStateFlow(initialState)
    val state
        @Composable
        get() = _state.asStateFlow().collectAsStateWithLifecycle().value

    init {
        middlewares.forEach { middleware ->
            middleware.setDispatcher(this)
            middleware.setScope(viewModelScope)
        }
        viewModelScope.launch {
            actions.onEach {
                Log.d(TAG, "actions: $it")
                middlewares.forEach { middleware ->
                    middleware.process(it.state, it.action)
                }
            }.collect()
        }
        viewModelScope.launch {
            actions.collect { action->
                _state.update {
                    reducer.run {
                        it.reduce(action.action)
                    }
                }
            }
        }
    }

    override fun state(): S = _state.value

    override fun dispatch(action: A) {
        val success = try {
            actions.tryEmit(ActionImpl(_state.value, action))
        } catch (e: Exception) {
            Log.e(TAG, "dispatch error", e)
            false
        }
        if (!success) error("MVI action buffer overflow")
    }

    override fun dispatchError(throwable: Throwable?) {
        viewModelScope.launch {
            _exception.emit(throwable)
        }
    }

    open fun onCreate() {

    }

    open fun onStart() {

    }

    override fun onCleared() {
        super.onCleared()
        middlewares.forEach { middleware ->
            middleware.onClear()
        }
    }
}