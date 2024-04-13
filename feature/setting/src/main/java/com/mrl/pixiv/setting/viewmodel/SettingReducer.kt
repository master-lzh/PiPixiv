package com.mrl.pixiv.setting.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer

class SettingReducer : Reducer<SettingState, SettingAction> {
    override fun reduce(state: SettingState, action: SettingAction): SettingState {
        return when (action) {
            is SettingAction.ChangeTheme -> {
                state.copy(theme = "Dark")
            }
        }
    }
}