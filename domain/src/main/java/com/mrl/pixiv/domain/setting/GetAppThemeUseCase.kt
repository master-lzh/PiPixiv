package com.mrl.pixiv.domain.setting

import com.mrl.pixiv.data.setting.SettingTheme
import com.mrl.pixiv.repository.local.SettingLocalRepository
import kotlinx.coroutines.flow.Flow

class GetAppThemeUseCase(
    private val settingLocalRepository: SettingLocalRepository
) {
    operator fun invoke(): Flow<SettingTheme> = settingLocalRepository.settingTheme
}