package com.mrl.pixiv.common.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.request.ImageRequest
import coil3.request.allowRgb565
import com.mrl.pixiv.common.util.throttleClick

@Composable
fun UserAvatar(
    url: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    contentDescription: String = "",
    contentScale: ContentScale = ContentScale.Fit,
) {
    if (url.isEmpty()) {
        CircularProgressIndicator(modifier)
    } else {
        LoadingImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .allowRgb565(true)
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
                .throttleClick(onClick = onClick)
                .clip(CircleShape),
            loadingContent = {
                CircularProgressIndicator(modifier)
            }
        )
    }
}
