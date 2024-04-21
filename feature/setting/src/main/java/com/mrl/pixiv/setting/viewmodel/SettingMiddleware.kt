package com.mrl.pixiv.setting.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.repository.local.SettingLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class SettingMiddleware(
    private val settingLocalRepository: SettingLocalRepository
) : Middleware<SettingState, SettingAction>() {
    override suspend fun process(state: SettingState, action: SettingAction) {
        when (action) {
            is SettingAction.LoadSetting -> loadSetting()
            is SettingAction.SwitchBypassSniffing -> switchBypassSniffing(state)
            is SettingAction.SavePictureSourceHost -> savePictureSourceHost(action.host)
            else -> Unit
        }
    }

    private fun savePictureSourceHost(host: String) {
        settingLocalRepository.setPictureSourceHost(host)
        dispatch(SettingAction.UpdatePictureSourceHost(host))
    }

    private fun switchBypassSniffing(state: SettingState) {
        settingLocalRepository.setEnableBypassSniffing(!state.enableBypassSniffing)
    }

    private fun loadSetting() {
        launchIO {
            settingLocalRepository.allSettings.flowOn(Dispatchers.Main).collect {
                dispatch(SettingAction.UpdateSetting(it))
            }
        }
    }
}