package com.mrl.pixiv.home.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable

@Composable
fun HomeTopBar(
    navigateToSearchScreen: () -> Unit,
    onRefreshToken: () -> Unit,
    onRefresh: () -> Unit,
) {
    IconButton(onClick = navigateToSearchScreen) {
        Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
    }
    IconButton(onClick = onRefreshToken) {
        Icon(imageVector = Icons.Rounded.Login, contentDescription = null)
    }
    IconButton(onClick = onRefresh) {
        Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null)
    }
}