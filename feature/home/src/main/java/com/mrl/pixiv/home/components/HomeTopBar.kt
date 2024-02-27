package com.mrl.pixiv.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
        Icon(imageVector = Icons.AutoMirrored.Rounded.Login, contentDescription = null)
    }
    IconButton(onClick = onRefresh) {
        Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null)
    }
}