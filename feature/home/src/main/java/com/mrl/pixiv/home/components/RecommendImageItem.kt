package com.mrl.pixiv.home.components

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowRgb565
import coil3.request.crossfade
import com.mrl.pixiv.common.ui.LocalAnimatedContentScope
import com.mrl.pixiv.common.ui.LocalSharedTransitionScope
import com.mrl.pixiv.common.ui.components.m3.IconButton
import com.mrl.pixiv.common.ui.components.m3.Surface
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.ui.item.BottomBookmarkSheet
import com.mrl.pixiv.common.ui.lightBlue
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.IllustAiType
import com.mrl.pixiv.data.Restrict
import com.mrl.pixiv.data.Type
import com.mrl.pixiv.domain.illust.GetIllustBookmarkDetailUseCase
import com.mrl.pixiv.util.throttleClick
import org.koin.compose.koinInject
import java.util.UUID
import kotlin.time.Duration.Companion.seconds


@Composable
fun RecommendImageItem(
    width: Dp,
    navToPictureScreen: (Illust, String) -> Unit,
    illust: Illust,
    isBookmarked: Boolean,
    onBookmarkClick: (String, List<String>?) -> Unit,
) {
    val scale = illust.height * 1.0f / illust.width
    val height = width * scale
    val sharedTransitionScope = LocalSharedTransitionScope.currentOrThrow
    val animatedContentScope = LocalAnimatedContentScope.currentOrThrow
    val prefix = rememberSaveable { UUID.randomUUID().toString() }
    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val getIllustBookmarkDetailUseCase = koinInject<GetIllustBookmarkDetailUseCase>()
    val onBookmarkLongClick = {
        showBottomSheet = true
    }
    val context = LocalContext.current
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
                val imageKey = "image-${illust.id}-0"
                AsyncImage(
                    model = remember {
                        ImageRequest.Builder(context)
                            .data(illust.imageUrls.medium).allowRgb565(true)
                            .crossfade(1.seconds.inWholeMilliseconds.toInt())
                            .placeholderMemoryCacheKey(imageKey)
                            .memoryCacheKey(imageKey)
                            .build()
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .width(width)
                        .height(height)
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .sharedElement(
                            sharedTransitionScope.rememberSharedContentState(key = "${prefix}-$imageKey"),
                            animatedVisibilityScope = animatedContentScope,
                            placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                        ),
                    alignment = Alignment.TopCenter,
                )
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
                            onBookmarkClick(Restrict.PUBLIC, null)
                        },
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "${prefix}-favorite-${illust.id}"),
                            animatedContentScope,
                            placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                        ),
                        onLongClick = onBookmarkLongClick
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
    BottomBookmarkSheet(
        showBottomSheet = showBottomSheet,
        hideBottomSheet = { showBottomSheet = false },
        getIllustBookmarkDetailUseCase = getIllustBookmarkDetailUseCase,
        illust = illust,
        bottomSheetState = bottomSheetState,
        onBookmarkClick = onBookmarkClick,
        isBookmarked = isBookmarked
    )
}