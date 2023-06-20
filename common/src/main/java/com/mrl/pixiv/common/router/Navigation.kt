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
    val title: String? = null,
    val icon: @Composable (() -> Unit)? = {},
) {
    object LoginScreen : Destination(route = "login_screen")

    object HomeScreen : Destination(
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

    object ProfileScreen : Destination(
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
}