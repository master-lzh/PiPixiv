package com.mrl.pixiv.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntryDecorator
import com.mrl.pixiv.common.repository.IllustCacheRepo
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.NavigationManager
import com.mrl.pixiv.common.router.NavigationRecord

/** Release picture sources only after the removed entry has finished its exit transition. */
@Composable
internal fun rememberPictureCacheDecorator(
    navigationManager: NavigationManager,
): NavEntryDecorator<NavigationRecord> {
    return remember(navigationManager) {
        val prefixes = mutableMapOf<Any, String>()
        NavEntryDecorator(
            onPop = { key ->
                prefixes.remove(key)?.let { prefix ->
                    val stillUsed = navigationManager.backStack.any {
                        (it.destination as? Destination.Picture)?.prefix == prefix
                    } || prefix in prefixes.values
                    if (!stillUsed) IllustCacheRepo.removeList(prefix)
                }
            },
        ) { entry ->
            val prefix = (entry.record.destination as? Destination.Picture)?.prefix
            DisposableEffect(entry.contentKey) {
                if (prefix != null) prefixes[entry.contentKey] = prefix
                onDispose { }
            }
            entry.Content()
        }
    }
}
