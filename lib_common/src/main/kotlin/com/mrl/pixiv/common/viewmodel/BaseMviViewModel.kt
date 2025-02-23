package com.mrl.pixiv.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 定义通用状态容器
sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
}

// 定义副作用（包括错误）
sealed interface SideEffect {
    data class Error(val throwable: Throwable) : SideEffect
    // 可扩展其他副作用如导航事件等
}

// 事件入口基类
interface ViewIntent

/**
 * MVI 架构基础 ViewModel
 * @param initialState 初始状态
 */
abstract class BaseMviViewModel<State : UiState<*>, Intent : ViewIntent>(
    initialState: State
) : ViewModel() {

    // 使用 StateFlow 管理状态
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    // 使用 SharedFlow 管理副作用（包括错误）
    private val _sideEffect = MutableSharedFlow<SideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    /**
     * 处理来自 UI 的意图
     */
    fun dispatch(intent: Intent) {
        viewModelScope.launch {
            try {
                handleIntent(intent)
            } catch (e: Exception) {
                // 自动捕获未处理异常并发送错误
                handleError(e)
            }
        }
    }

    /**
     * 抽象方法 - 具体业务逻辑处理
     */
    protected abstract suspend fun handleIntent(intent: Intent)

    /**
     * 更新状态（使用 reducer 模式保证不可变性）
     */
    protected fun updateState(reducer: State.() -> State) {
        _state.update { it.reducer() }
    }

    /**
     * 发送副作用（包括错误）
     */
    protected suspend fun sendEffect(effect: SideEffect) {
        _sideEffect.emit(effect)
    }

    /**
     * 统一错误处理
     */
    protected suspend fun handleError(throwable: Throwable) {
        sendEffect(SideEffect.Error(throwable))
    }
}