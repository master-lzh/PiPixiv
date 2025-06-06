package com.mrl.pixiv.common.compose.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrl.pixiv.common.util.throttleClick

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    icon: @Composable (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
    content: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .throttleClick(indication = ripple()) { onClick() }
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        icon?.invoke()
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            content()
        }
    }
}