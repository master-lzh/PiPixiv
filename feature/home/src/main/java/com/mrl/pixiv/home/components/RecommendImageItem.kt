package com.mrl.pixiv.home.components

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FileCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.common.ui.LocalAnimatedContentScope
import com.mrl.pixiv.common.ui.LocalSharedTransitionScope
import com.mrl.pixiv.common.ui.components.m3.Surface
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.ui.lightBlue
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.IllustAiType
import com.mrl.pixiv.data.Type
import com.mrl.pixiv.util.DisplayUtil
import com.mrl.pixiv.util.throttleClick
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

val SPACING_HORIZONTAL_DP = 5.dp
val SPACING_VERTICAL_DP = 5.dp
const val INCLUDE_EDGE = true


@Composable
fun RecommendImageItem(
    navToPictureScreen: (Illust, String) -> Unit,
    illust: Illust,
    bookmarkState: BookmarkState,
    onBookmarkClick: (id: Long, bookmark: Boolean) -> Unit,
    spanCount: Int,
) {
    val width =
        (DisplayUtil.getScreenWidthDp() - SPACING_HORIZONTAL_DP * (spanCount + if (INCLUDE_EDGE) 1 else -1)) / spanCount
    val scale = illust.height * 1.0f / illust.width
    val height = width * scale
    val isBookmarked = bookmarkState.bookmarkStatus[illust.id] ?: illust.isBookmarked
    val sharedTransitionScope = LocalSharedTransitionScope.currentOrThrow
    val animatedContentScope = LocalAnimatedContentScope.currentOrThrow
    val prefix = rememberSaveable { UUID.randomUUID().toString() }
    with(sharedTransitionScope) {
        Surface(
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(key = "${prefix}-card-${illust.id}"),
                    animatedContentScope,
                    clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(10.dp))
                )
                .padding(horizontal = 5.dp)
                .padding(bottom = 5.dp)
                .throttleClick {
                    navToPictureScreen(illust, prefix)
                },
            shape = RoundedCornerShape(10.dp),
            shadowElevation = 4.dp,
            propagateMinConstraints = false,
        ) {
            Column {
                Box {
                    val imageKey = "image-${illust.id}-0"
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(illust.imageUrls.medium).allowRgb565(true)
                            .transformations(
                                with(LocalDensity.current) {
                                    RoundedCornersTransformation(
                                        topLeft = 10.dp.toPx(),
                                        topRight = 10.dp.toPx()
                                    )
                                }
                            )
//                        .scale(Scale.FIT)
                            .crossfade(1.seconds.inWholeMilliseconds.toInt())
                            .placeholderMemoryCacheKey(imageKey)
                            .memoryCacheKey(imageKey)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .width(width)
                            .height(height)
                            .sharedElement(
                                sharedTransitionScope.rememberSharedContentState(key = "${prefix}-$imageKey"),
                                animatedVisibilityScope = animatedContentScope,
                                placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                            )
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                        filterQuality = FilterQuality.None,
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = illust.title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .sharedElement(
                                    rememberSharedContentState(key = "${prefix}-title-${illust.id}"),
                                    animatedContentScope,
                                    placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                                )
                                .skipToLookaheadSize(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Text(
                            text = illust.user.name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .sharedElement(
                                    rememberSharedContentState(key = "${prefix}-user-name-${illust.user.id}"),
                                    animatedContentScope,
                                    placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                                )
                                .skipToLookaheadSize(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    IconButton(
                        onClick = {
                            onBookmarkClick(illust.id, isBookmarked)
                        },
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "${prefix}-favorite-${illust.id}"),
                            animatedContentScope,
                            placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                        )
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "",
                            modifier = Modifier.size(24.dp),
                            tint = if (isBookmarked) Color.Red else Color.Gray
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (illust.illustAIType == IllustAiType.AiGeneratedWorks) {
                    Text(
                        text = "AI",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .background(lightBlue, MaterialTheme.shapes.small)
                            .padding(horizontal = 5.dp)
                    )
                }
                if (illust.type == Type.Ugoira) {
                    Row(
                        modifier = Modifier
                            .background(lightBlue, MaterialTheme.shapes.small)
                            .padding(horizontal = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "GIF", color = Color.White, fontSize = 10.sp)
                    }
                }
                if (illust.pageCount > 1) {
                    Row(
                        modifier = Modifier
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FileCopy,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(text = "${illust.pageCount}", color = Color.White, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}