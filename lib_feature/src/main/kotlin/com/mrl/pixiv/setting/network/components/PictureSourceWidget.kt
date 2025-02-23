package com.mrl.pixiv.setting.network.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrl.pixiv.common.ui.components.m3.Surface
import com.mrl.pixiv.common.ui.components.m3.TextField
import com.mrl.pixiv.common.ui.item.SettingItem
import com.mrl.pixiv.common.util.AppUtil
import com.mrl.pixiv.common.util.RString

@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
fun PictureSourceWidget(
    modifier: Modifier = Modifier,
    currentSelected: String = "",
    savePictureSourceHost: (String) -> Unit = {}
) {
    val map = remember {
        mapOf(
            "i.pximg.net" to "${AppUtil.getString(RString.label_default)}: i.pximg.net",
            "i.pixiv.cat" to "i.pixiv.cat",
            "i.pixiv.re" to "i.pixiv.re"
        )
    }
    val focusManager = LocalFocusManager.current
    var imageHost by remember { mutableStateOf(TextFieldValue(currentSelected)) }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column {
            SettingItem {
                Text(text = stringResource(RString.image_source))
                IconButton(
                    onClick = {
                        val host = map.entries.first().key
                        savePictureSourceHost(host)
                        imageHost = imageHost.copy(text = host)
                    }
                ) {
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
                        focusManager.clearFocus()
                        savePictureSourceHost(key)
                        imageHost = imageHost.copy(text = key)
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
                val imeVisible = WindowInsets.isImeVisible
                LaunchedEffect(WindowInsets.isImeVisible) {
                    if (!imeVisible) {
                        focusManager.clearFocus()
                    }
                }
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = imageHost,
                    onValueChange = { imageHost = it },
                    singleLine = true,
                    label = { Text(text = stringResource(RString.self_defined_source)) },
                    trailingIcon = {
                        IconButton(onClick = { savePictureSourceHost(imageHost.text) }) {
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