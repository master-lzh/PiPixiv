package com.mrl.pixiv.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrl.pixiv.common.ui.components.TextField
import com.mrl.pixiv.util.click

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchTopBar(
    textState: MutableState<TextFieldValue>,
    popBack: () -> Unit,
    searchIllust: () -> Unit,
) {
    var isSearchFieldVisible by remember { mutableStateOf(false) }
    val tags = textState.value.text.split(" ").filter { it.isNotBlank() }.toMutableList()
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(isSearchFieldVisible) {
        if (isSearchFieldVisible) {
            focusRequester.requestFocus()
        }
    }
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = popBack) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp),
            color = Color(0xFFE0E0E0),
            shape = MaterialTheme.shapes.small
        ) {
            Box(
                modifier = Modifier,
            ) {
                if (isSearchFieldVisible) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .click {
                                focusRequester.requestFocus()
                            },
                        minHeight = 40.dp,
                        contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
                            top = 2.dp,
                            bottom = 2.dp
                        ),
                        value = textState.value,
                        onValueChange = {
                            textState.value = it
                        },
                        placeholder = { Text("输入关键字") },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color(0xFFE0E0E0),
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                        ),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                textState.value = textState.value.copy("")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear"
                                )
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                textState.value = textState.value.copy(tags.joinToString(" "))
                                focusRequester.freeFocus()
                                isSearchFieldVisible = false
                                searchIllust()
                            }
                        )
                    )
                } else {
                    LazyRow(
                        modifier = Modifier
                            .heightIn(min = 56.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                            .click {
                                isSearchFieldVisible = true
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        itemsIndexed(
                            tags,
                            key = { index, item -> "${index}_${item}" }) { index, tag ->
                            TagChip(index, tag) {
                                tags.removeAt(index)
                                textState.value = textState.value.copy(tags.joinToString(" "))
                                // todo 删除后执行搜索操作
                            }
                        }
                    }
                }

            }
        }

    }
}

@Preview
@Composable
fun TagChip(index: Int = 0, tag: String = "ssadas", onRemoveTag: (index: Int) -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.Blue)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = tag,
            color = Color.White,
            modifier = Modifier.padding(end = 4.dp)
        )
        Icon(
            modifier = Modifier.click {
                onRemoveTag(index)
            },
            imageVector = Icons.Default.Close,
            contentDescription = "Remove",
            tint = Color.White
        )
    }
    Spacer(modifier = Modifier.width(8.dp)) // Add space between chips
}