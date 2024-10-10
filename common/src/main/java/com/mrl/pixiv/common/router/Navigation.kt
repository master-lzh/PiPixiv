package com.mrl.pixiv.common.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mrl.pixiv.common.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class Destination(
    val route: String,
    @Transient
    val title: @Composable () -> String = { "" },
    @Transient
    val icon: @Composable (() -> Unit)? = {},
) {
    @Serializable
    data object LoginScreen : Destination(route = "login_screen")

    @Serializable
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

    @Serializable
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

    @Serializable
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

    @Serializable
    data object SelfProfileDetailScreen : Destination(route = "self_profile_detail_screen")

    @Serializable
    data class OtherProfileDetailScreen(
        val userId: Long,
    ) : Destination(route = "other_profile_detail_screen")

    @Serializable
    data class PictureScreen(
        val illustId: Long,
        val prefix: String,
    ) : Destination(route = "picture_screen")

    @Serializable
    data class PictureDeeplinkScreen(
        val illustId: Long,
    ) : Destination(route = "picture_deeplink_screen")

    @Serializable
    data object SearchScreen : Destination(route = "search_screen")

    @Serializable
    data class SearchResultsScreen(
        val searchWord: String,
    ) : Destination(route = "search_results_screen")

    @Serializable
    data object SettingScreen : Destination(route = "setting_screen")

    @Serializable
    data object NetworkSettingScreen : Destination(route = "network_setting_screen")

    @Serializable
    data object HistoryScreen : Destination(route = "history_screen")

    @Serializable
    data object SelfCollectionScreen : Destination(route = "self_collection_screen")
}