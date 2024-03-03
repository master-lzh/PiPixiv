package com.mrl.pixiv.common.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import coil.transform.Transformation
import com.mrl.pixiv.util.throttleClick

@Composable
fun UserAvatar(
    url: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    onClick: () -> Unit = {},
    contentDescription: String = "",
    contentScale: ContentScale = ContentScale.Fit,
    tint: Color = Color.Unspecified,
    transformations: List<Transformation> = listOf(CircleCropTransformation()),
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url).allowRgb565(true)
            .transformations(transformations)
            .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
            .size(size)
            .throttleClick(onClick = onClick)
    )
}
