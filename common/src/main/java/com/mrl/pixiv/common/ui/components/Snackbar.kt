package com.mrl.pixiv.common.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextSnackbar(
    text: String,
    singleLine: Boolean = true,
    hasNavigationBar: Boolean = true,
    action: @Composable () -> Unit = {},
) {
    Snackbar(
        action = action,
        modifier = Modifier.padding(
            bottom = if (hasNavigationBar) 56.dp else 0.dp,
            start = 16.dp,
            end = 16.dp
        )
    ) {
        Text(
            text = text,
            maxLines = if (singleLine) {
                1
            } else {
                Int.MAX_VALUE
            },
        )
    }
}

