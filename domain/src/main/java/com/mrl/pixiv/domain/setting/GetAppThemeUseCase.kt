package com.mrl.pixiv.domain.setting

import com.mrl.pixiv.data.setting.SettingTheme
import com.mrl.pixiv.repository.SettingRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class GetAppThemeUseCase(
    private val settingRepository: SettingRepository
) {
    operator fun invoke(): Flow<SettingTheme> = settingRepository.settingTheme
}