package com.mrl.pixiv.setting.network.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrl.pixiv.common.ui.components.m3.Surface
import com.mrl.pixiv.common.ui.components.m3.TextField
import com.mrl.pixiv.setting.components.SettingItem

private val map = mapOf(
    "i.pximg.net" to "默认: i.pximg.net",
    "i.pixiv.cat" to "i.pixiv.cat",
    "i.pixiv.re" to "i.pixiv.re"
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PictureSourceWidget(
    modifier: Modifier = Modifier,
    currentSelected: String = "",
    savePictureSourceHost: (String) -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column {
            SettingItem {
                Text(text = "图源")
                IconButton(onClick = { savePictureSourceHost(map.entries.first().key) }) {
                    Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null)
                }
            }
            map.forEach { (key, value) ->
                SettingItem(
                    modifier = Modifier.then(
                        if (key == currentSelected) {
                            Modifier.background(MaterialTheme.colorScheme.primary)
                        } else Modifier
                    ),
                    onClick = {
                        savePictureSourceHost(key)
                    },
                ) {
                    Text(text = value)
                    if (key == currentSelected) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
            SettingItem {
                var value by remember { mutableStateOf(TextFieldValue(currentSelected)) }
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value,
                    onValueChange = { value = it },
                    singleLine = true,
                    label = { Text(text = "自定义图源") },
                    trailingIcon = {
                        IconButton(onClick = { savePictureSourceHost(value.text) }) {
                            Icon(imageVector = Icons.Rounded.Check, contentDescription = null)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                )
            }
        }
    }
}