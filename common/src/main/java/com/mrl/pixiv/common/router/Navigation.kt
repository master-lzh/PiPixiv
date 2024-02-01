package com.mrl.pixiv.common.router

import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
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
                tint = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
            )
        }
    )

    data object ProfileScreen : Destination(
        route = "profile_screen",
        title = "我的",
        icon = {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = null,
                tint = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
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