package com.mrl.pixiv.repository.local

import android.os.Build
import com.mrl.pixiv.data.setting.SettingTheme
import com.mrl.pixiv.datasource.local.SettingDataSource
import kotlinx.coroutines.flow.map

class SettingLocalRepository(
    private val settingDataSource: SettingDataSource
) {
    val settingTheme = settingDataSource.settingTheme.get(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) SettingTheme.SYSTEM.toString() else SettingTheme.LIGHT.toString()
    ).map {
        enumValueOf<SettingTheme>(it)
    }

    fun setSettingTheme(theme: SettingTheme) = settingDataSource.settingTheme.set(theme.toString())
}