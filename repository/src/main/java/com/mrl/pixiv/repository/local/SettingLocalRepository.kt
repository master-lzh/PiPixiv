package com.mrl.pixiv.repository.local

import com.mrl.pixiv.data.setting.SettingTheme
import com.mrl.pixiv.datasource.local.SettingDataSource
import kotlinx.coroutines.flow.map

class SettingLocalRepository(
    private val settingDataSource: SettingDataSource
) {
    val settingTheme = settingDataSource.settingTheme.get(SettingTheme.SYSTEM.toString()).map {
        enumValueOf<SettingTheme>(it)
    }

    fun setSettingTheme(theme: SettingTheme) = settingDataSource.settingTheme.set(theme.toString())
}