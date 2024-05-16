package com.mrl.pixiv.common.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mrl.pixiv.common.R

sealed class Destination(
    val route: String,
    val title: @Composable () -> String = { "" },
    val icon: @Composable (() -> Unit)? = {},
) {
    data object LoginScreen : Destination(route = "login_screen")

    data object HomeScreen : Destination(
        route = "home_screen",
        title = { stringResource(R.string.home) },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Home,
                contentDescription = null,
            )
        }
    )

    data object SearchPreviewScreen : Destination(
        route = "search_preview_screen",
        title = { stringResource(R.string.search) },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
            )
        }
    )

    data object ProfileScreen : Destination(
        route = "profile_screen",
        title = { stringResource(R.string.my) },
        icon = {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = null,
            )
        }
    )

    data object SelfProfileDetailScreen : Destination(route = "self_profile_detail_screen")

    data object OtherProfileDetailScreen : Destination(route = "other_profile_detail_screen") {
        const val userId = "userId"
    }

    data object PictureScreen : Destination(route = "picture_screen") {
        const val illustId = "illustId"
        const val prefix = "prefix"
    }

    data object PictureDeeplinkScreen : Destination(route = "picture_deeplink_screen") {
        const val illustId = "illustId"
    }

    data object SearchScreen : Destination(route = "search_screen")

    data object SearchResultsScreen : Destination(route = "search_results_screen") {
        const val searchWord = "search_keyword"
    }

    data object SettingScreen : Destination(route = "setting_screen")

    data object NetworkSettingScreen : Destination(route = "network_setting_screen")

    data object HistoryScreen : Destination(route = "history_screen")

    data object SelfCollectionScreen : Destination(route = "self_collection_screen")
}