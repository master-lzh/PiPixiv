package com.mrl.pixiv.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

class SettingDataSource(
    settingDataSource: DataStore<Preferences>
) : BaseDataSource(settingDataSource) {
    companion object {
        val KEY_SETTING_THEME = stringPreferencesKey("setting_theme")
    }

    val settingTheme = createFiled(KEY_SETTING_THEME)
}

val Context.settingDataStore: DataStore<Preferences> by preferencesDataStore(name = "setting")
