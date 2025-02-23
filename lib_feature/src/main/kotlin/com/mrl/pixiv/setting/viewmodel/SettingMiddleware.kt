package com.mrl.pixiv.setting.viewmodel

import com.mrl.pixiv.common.repository.SettingRepository
import com.mrl.pixiv.common.viewmodel.Middleware
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import org.koin.core.annotation.Factory

@Factory
class SettingMiddleware(
    private val settingRepository: SettingRepository
) : Middleware<SettingState, SettingAction>() {
    override suspend fun process(state: SettingState, action: SettingAction) {
        when (action) {
            is SettingAction.LoadSetting -> loadSetting()
            is SettingAction.SwitchBypassSniffing -> switchBypassSniffing()
            is SettingAction.SavePictureSourceHost -> savePictureSourceHost(action.host)
            else -> Unit
        }
    }

    private fun savePictureSourceHost(host: String) {
        settingRepository.setPictureSourceHost(host)
        dispatch(SettingAction.UpdatePictureSourceHost(host))
    }

    private fun switchBypassSniffing() {
        settingRepository.setEnableBypassSniffing(!state().enableBypassSniffing)
    }

    private fun loadSetting() {
        launchIO {
            settingRepository.allSettings.flowOn(Dispatchers.Main).collect {
                dispatch(SettingAction.UpdateSetting(it))
            }
        }
    }
}