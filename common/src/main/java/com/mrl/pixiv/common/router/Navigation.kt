package com.mrl.pixiv.common.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

sealed class Destination(
    val route: String,
    val title: String = "",
    val icon: @Composable (() -> Unit)? = {},
) {
    data object LoginScreen : Destination(route = "login_screen")

    data object HomeScreen : Destination(
        route = "home_screen",
        title = "首页",
        icon = {
            Icon(
                imageVector = Icons.Rounded.Home,
                contentDescription = null,
            )
        }
    )

    data object SearchPreviewScreen : Destination(
        route = "search_preview_screen",
        title = "搜索",
        icon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
            )
        }
    )

    data object ProfileDetailScreen : Destination(
        route = "profile_detail_screen",
        title = "我的",
        icon = {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = null,
            )
        }
    )

    data object PictureScreen : Destination(route = "picture_screen") {
        const val illustParams = "illust"
    }

    data object SearchScreen : Destination(route = "search_screen")

    data object SearchResultsScreen : Destination(route = "search_results_screen") {
        const val searchWord = "search_keyword"
    }
}