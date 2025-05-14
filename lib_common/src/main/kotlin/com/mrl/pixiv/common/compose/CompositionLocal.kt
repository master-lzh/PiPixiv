package com.mrl.pixiv.common.compose

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavigator = staticCompositionLocalOf<NavHostController> {
    noLocalProvidedFor("LocalNavigator")
}

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
    noLocalProvidedFor("LocalSharedTransitionScope")
}

val LocalAnimatedContentScope = compositionLocalOf<AnimatedContentScope> {
    noLocalProvidedFor("LocalAnimatedContentScope")
}

val LocalSharedKeyPrefix = compositionLocalOf {
    ""
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}