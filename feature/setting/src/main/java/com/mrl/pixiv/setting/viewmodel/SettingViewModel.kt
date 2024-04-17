package com.mrl.pixiv.setting.viewmodel

import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.setting.UserPreference

data class SettingState(
    val enableBypassSniffing: Boolean
) : State {
    companion object {
        val INITIAL = SettingState(
            enableBypassSniffing = false
        )
    }
}

sealed class SettingAction : Action {
    data object LoadSetting : SettingAction()
    data object SwitchBypassSniffing : SettingAction()
    data class UpdateSetting(val setting: UserPreference) : SettingAction()
}

class SettingViewModel(
    reducer: SettingReducer,
    middleware: SettingMiddleware,
) : BaseViewModel<SettingState, SettingAction>(
    initialState = SettingState.INITIAL,
    reducer = reducer,
    middlewares = listOf(middleware),
) {
    init {
        dispatch(SettingAction.LoadSetting)
    }
}