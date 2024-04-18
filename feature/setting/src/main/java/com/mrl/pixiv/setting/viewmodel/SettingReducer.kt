package com.mrl.pixiv.setting.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer

class SettingReducer : Reducer<SettingState, SettingAction> {
    override fun reduce(state: SettingState, action: SettingAction): SettingState {
        return when (action) {
            is SettingAction.SwitchBypassSniffing -> state.copy(enableBypassSniffing = !state.enableBypassSniffing)
            is SettingAction.UpdateSetting -> state.copy(enableBypassSniffing = action.setting.enableBypassSniffing)
            else -> state
        }
    }
}