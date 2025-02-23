package com.mrl.pixiv.di

import com.mrl.pixiv.AppModule
import com.mrl.pixiv.FeatureModule
import com.mrl.pixiv.common.CommonModule
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import org.koin.ksp.generated.module

@Single
fun provideJson() = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    isLenient = true
}

val allModule = listOf(
    AppModule.module,
    CommonModule.module,
    FeatureModule.module,
)
