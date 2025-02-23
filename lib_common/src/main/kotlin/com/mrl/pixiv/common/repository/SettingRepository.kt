package com.mrl.pixiv.common.repository

import android.os.Build
import com.mrl.pixiv.common.data.setting.SettingTheme
import com.mrl.pixiv.common.datasource.local.datastore.SettingDataSource
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class SettingRepository(
    private val settingDataSource: SettingDataSource
) {
    val allSettings = settingDataSource.data
    val allSettingsSync = settingDataSource.syncData

    val settingTheme = settingDataSource.data.map {
        enumValueOf<SettingTheme>(
            it.theme.ifEmpty {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) SettingTheme.SYSTEM.toString() else SettingTheme.LIGHT.toString()
            }
        )
    }

    fun setSettingTheme(theme: SettingTheme) = settingDataSource.updateData {
        it.copy(theme = theme.toString())
    }

    val enableBypassSniffing = settingDataSource.data.map { it.enableBypassSniffing }
    fun setEnableBypassSniffing(enable: Boolean) = settingDataSource.updateData {
        it.copy(enableBypassSniffing = enable)
    }

    val pictureSourceHost = settingDataSource.data.map { it.imageHost }
    fun setPictureSourceHost(host: String) = settingDataSource.updateData {
        it.copy(imageHost = host)
    }

    val hasShowBookmarkTip = settingDataSource.data.map { it.hasShowBookmarkTip }
    fun setHasShowBookmarkTip(hasShow: Boolean) = settingDataSource.updateData {
        it.copy(hasShowBookmarkTip = hasShow)
    }
}