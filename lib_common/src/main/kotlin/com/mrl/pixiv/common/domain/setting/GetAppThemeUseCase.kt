package com.mrl.pixiv.common.domain.setting

import com.mrl.pixiv.common.data.setting.SettingTheme
import com.mrl.pixiv.common.repository.SettingRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class GetAppThemeUseCase(
    private val settingRepository: SettingRepository
) {
    operator fun invoke(): Flow<SettingTheme> = settingRepository.settingTheme
}