package com.mrl.pixiv.common.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject

/** Provided by each NavEntry so events retain the identity of the page that produced them. */
val LocalNavigationManager = staticCompositionLocalOf<NavigationManager?> { null }

@Composable
fun currentNavigationManager(): NavigationManager =
    LocalNavigationManager.current ?: koinInject()

/** Registers live navigation state with the host without replacing the shared Koin instance. */
@Composable
fun rememberNavigationState(navigationManager: NavigationManager): NavigationManager {
    val saver = remember(navigationManager) {
        Saver<NavigationManager, String>(
            save = {
                Json.encodeToString(NavigationStateSnapshot.serializer(), it.saveState())
            },
            restore = { saved ->
                runCatching {
                    val snapshot = Json.decodeFromString(NavigationStateSnapshot.serializer(), saved)
                    navigationManager.restoreState(snapshot)
                    navigationManager
                }.getOrNull()
            },
        )
    }
    return rememberSaveable(navigationManager, saver = saver) { navigationManager }
}
