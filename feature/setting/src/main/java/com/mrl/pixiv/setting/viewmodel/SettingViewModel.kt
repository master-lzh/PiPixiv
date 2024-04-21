package com.mrl.pixiv.setting.viewmodel

import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.setting.UserPreference

data class SettingState(
    val enableBypassSniffing: Boolean,
    val pictureSourceHost: String
) : State {
    companion object {
        val INITIAL = SettingState(
            enableBypassSniffing = false,
            pictureSourceHost = ""
        )
    }
}

sealed class SettingAction : Action {
    data object LoadSetting : SettingAction()
    data object SwitchBypassSniffing : SettingAction()
    data class SavePictureSourceHost(val host: String) : SettingAction()


    data class UpdateSetting(val setting: UserPreference) : SettingAction()
    data class UpdatePictureSourceHost(val host: String) : SettingAction()
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