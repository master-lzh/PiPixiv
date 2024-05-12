package com.mrl.pixiv.common.ui.components

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import coil.transform.Transformation
import com.mrl.pixiv.common.ui.LocalAnimatedContentScope
import com.mrl.pixiv.common.ui.LocalSharedTransitionScope
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.util.throttleClick
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
    enableSharedElement: Boolean = false
) {
    with(LocalSharedTransitionScope.currentOrThrow) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url).allowRgb565(true)
                .memoryCacheKey(url)
                .transformations(transformations)
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
                .then(
                    if (enableSharedElement) {
                        Modifier.sharedElement(
                            rememberSharedContentState(key = "profile_image_$url"),
                            LocalAnimatedContentScope.currentOrThrow,
                            placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                        )
                    } else {
                        Modifier
                    }
                )
                .throttleClick(onClick = onClick)
        )
    }
}
