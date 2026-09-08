package com.mrl.pixiv.collection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.NavigationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
class CollectionPagerStateTest {
    @Test
    fun switchingCollectionTypeClosesTheOldDetailButRestoringThePageDoesNot() {
        Dispatchers.setMain(Dispatchers.Swing)
        try {
            runDesktopComposeUiTest(testTimeout = 30.seconds) {
                val navigation = NavigationManager(Destination.Main)
                navigation.navigateToCollectionScreen(7, isNovel = true)
                val collection = navigation.backStack.last()
                val collectionNavigation = navigation.forEntry(collection.entryId)
                collectionNavigation.navigateToNovelDetailScreen(91)
                val restoredDetail = navigation.backStack.last()
                lateinit var selectPage: (Int) -> Unit

                setContent {
                    val pager = rememberCollectionPagerState(1, collectionNavigation)
                    val scope = rememberCoroutineScope()
                    selectPage = { page -> scope.launch { pager.scrollToPage(page) } }
                    HorizontalPager(pager, modifier = Modifier.fillMaxSize()) {
                        Box(Modifier.fillMaxSize())
                    }
                }
                waitForIdle()
                runOnIdle { assertEquals(restoredDetail, navigation.backStack.last()) }

                runOnIdle { selectPage(0) }
                waitForIdle()
                runOnIdle {
                    assertEquals(collection, navigation.backStack.last())
                    assertEquals(2, navigation.backStack.size)
                    collectionNavigation.navigateToProfileDetailScreen(8)
                }
                waitForIdle()
                runOnIdle { assertEquals(Destination.ProfileDetail(8), navigation.currentDestination) }

                runOnIdle { selectPage(1) }
                waitForIdle()
                runOnIdle { assertEquals(collection, navigation.backStack.last()) }
            }
        } finally {
            Dispatchers.resetMain()
        }
    }
}
