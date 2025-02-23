package com.mrl.pixiv.common.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowRgb565
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import coil3.transform.Transformation
import com.mrl.pixiv.common.util.throttleClick
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun UserAvatar(
    url: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    contentDescription: String = "",
    contentScale: ContentScale = ContentScale.Fit,
    tint: Color = Color.Unspecified,
    transformations: ImmutableList<Transformation> = persistentListOf(CircleCropTransformation()),
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url).allowRgb565(true)
            .transformations(transformations)
            .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier.throttleClick(onClick = onClick)
    )
}
