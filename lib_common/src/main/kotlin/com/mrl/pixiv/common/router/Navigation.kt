package com.mrl.pixiv.common.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mrl.pixiv.common.util.RString
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class Destination(
    @Transient
    val title: @Composable () -> String = { "" },
    @Transient
    val icon: @Composable() (() -> Unit)? = {},
) {
    @Serializable
    data object LoginScreen : Destination()

    @Serializable
    data object HomeScreen : Destination(
        title = { stringResource(RString.home) },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Home,
                contentDescription = null,
            )
        }
    )

    @Serializable
    data object SearchPreviewScreen : Destination(
        title = { stringResource(RString.search) },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
            )
        }
    )

    @Serializable
    data object ProfileScreen : Destination(
        title = { stringResource(RString.my) },
        icon = {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = null,
            )
        }
    )

    @Serializable
    data object SelfProfileDetailScreen : Destination()

    @Serializable
    data class OtherProfileDetailScreen(
        val userId: Long,
    ) : Destination()

    @Serializable
    data class PictureScreen(
        val illustId: Long,
        val prefix: String,
    ) : Destination()

    @Serializable
    data class PictureDeeplinkScreen(
        val illustId: Long,
    ) : Destination()

    @Serializable
    data object SearchScreen : Destination()

    @Serializable
    data class SearchResultsScreen(
        val searchWords: String,
    ) : Destination()

    @Serializable
    data object SettingScreen : Destination()

    @Serializable
    data object NetworkSettingScreen : Destination()

    @Serializable
    data object HistoryScreen : Destination()

    @Serializable
    data object SelfCollectionScreen : Destination()

    @Serializable
    data class FollowingScreen(
        val userId: Long,
    ) : Destination()

    @Serializable
    data class HorizontalPictureScreen(
        val illusts: String,
        val index: Int,
        val prefix: String,
    )
}