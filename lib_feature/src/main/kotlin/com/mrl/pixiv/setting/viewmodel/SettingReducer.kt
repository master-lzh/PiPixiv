package com.mrl.pixiv.setting.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import org.koin.core.annotation.Single

@Single
class SettingReducer : Reducer<SettingState, SettingAction> {
    override fun SettingState.reduce(action: SettingAction): SettingState {
        return when (action) {
            is SettingAction.SwitchBypassSniffing -> copy(enableBypassSniffing = !enableBypassSniffing)
            is SettingAction.UpdateSetting -> copy(
                enableBypassSniffing = action.setting.enableBypassSniffing,
                pictureSourceHost = action.setting.imageHost
            )

            is SettingAction.UpdatePictureSourceHost -> copy(pictureSourceHost = action.host)
            else -> this
        }
    }
}