package com.mrl.pixiv.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DropDownSelector(
    modifier: Modifier = Modifier,
    current: String,
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), MaterialTheme.shapes.small)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = current, style = MaterialTheme.typography.bodyMedium)
        Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null)
    }
}