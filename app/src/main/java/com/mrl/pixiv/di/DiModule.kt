package com.mrl.pixiv.di

import com.mrl.pixiv.AppModule
import com.mrl.pixiv.collection.CollectionModule
import com.mrl.pixiv.common.CommonModule
import com.mrl.pixiv.datasource.DatasourceModule
import com.mrl.pixiv.domain.DomainModule
import com.mrl.pixiv.history.HistoryModule
import com.mrl.pixiv.home.HomeModule
import com.mrl.pixiv.login.LoginModule
import com.mrl.pixiv.picture.PictureModule
import com.mrl.pixiv.profile.ProfileModule
import com.mrl.pixiv.repository.RepositoryModule
import com.mrl.pixiv.search.SearchModule
import com.mrl.pixiv.setting.SettingModule
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import org.koin.ksp.generated.module

@Single
fun provideJson() = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    isLenient = true
}


val featureModule = arrayOf(
    CollectionModule.module,
    HistoryModule.module,
    HomeModule.module,
    LoginModule.module,
    PictureModule.module,
    ProfileModule.module,
    SearchModule.module,
    SettingModule.module,
)

val allModule = listOf(
    AppModule.module,
    CommonModule.module,
    RepositoryModule.module,
    DatasourceModule.module,
    DomainModule.module,
    *featureModule,
)
