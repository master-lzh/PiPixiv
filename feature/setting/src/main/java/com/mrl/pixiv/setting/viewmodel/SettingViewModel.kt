package com.mrl.pixiv.setting.viewmodel

import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State

data class SettingState(
    val theme: String
) : State {
    companion object {
        val INITIAL = SettingState(
            theme = "Light"
        )
    }
}

sealed class SettingAction : Action {
    data object ChangeTheme : SettingAction()
}

class SettingViewModel(
    reducer: SettingReducer,
    middleware: SettingMiddleware,
) : BaseViewModel<SettingState, SettingAction>(
    initialState = SettingState.INITIAL,
    reducer = reducer,
    middlewares = listOf(middleware),
) {
}