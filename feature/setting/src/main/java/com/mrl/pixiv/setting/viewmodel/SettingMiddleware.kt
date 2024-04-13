package com.mrl.pixiv.setting.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware

class SettingMiddleware : Middleware<SettingState, SettingAction>() {
    override suspend fun process(state: SettingState, action: SettingAction) {
        when (action) {
            is SettingAction.ChangeTheme -> {

            }
        }
    }
}