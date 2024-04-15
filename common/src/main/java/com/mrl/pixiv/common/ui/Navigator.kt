package com.mrl.pixiv.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavigator = staticCompositionLocalOf<NavHostController?> {
    null
}

val <T> ProvidableCompositionLocal<T?>.currentOrThrow: T
    @Composable
    get() = this.current ?: error("No NavHostController provided")