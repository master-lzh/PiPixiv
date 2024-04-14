package com.mrl.pixiv.repository.local

import com.mrl.pixiv.data.setting.SettingTheme
import com.mrl.pixiv.datasource.local.SettingDataSource
import kotlinx.coroutines.flow.map

class SettingLocalRepository(
    private val settingDataSource: SettingDataSource
) {
    val settingTheme = settingDataSource.settingTheme.get("system").map {
        when (it) {
            "light" -> SettingTheme.LIGHT
            "dark" -> SettingTheme.DARK
            else -> SettingTheme.SYSTEM
        }
    }

    fun setSettingTheme(theme: SettingTheme) {
        settingDataSource.settingTheme.set(
            when (theme) {
                SettingTheme.LIGHT -> "light"
                SettingTheme.DARK -> "dark"
                else -> "system"
            }
        )
    }
}