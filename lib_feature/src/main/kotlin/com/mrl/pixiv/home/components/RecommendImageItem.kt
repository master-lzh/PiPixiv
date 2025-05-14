package com.mrl.pixiv.home.components

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FileCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowRgb565
import coil3.request.crossfade
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.data.IllustAiType
import com.mrl.pixiv.common.data.Restrict
import com.mrl.pixiv.common.data.Type
import com.mrl.pixiv.common.kts.round
import com.mrl.pixiv.common.ui.LocalAnimatedContentScope
import com.mrl.pixiv.common.ui.LocalSharedTransitionScope
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.ui.item.BottomBookmarkSheet
import com.mrl.pixiv.common.ui.lightBlue
import com.mrl.pixiv.common.util.throttleClick
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid


@Composable
fun RecommendImageItem(
    navToPictureScreen: (String) -> Unit,
    illust: Illust,
    isBookmarked: Boolean,
    onBookmarkClick: (String, List<String>?) -> Unit,
) {
    val scale = illust.width * 1.0f / illust.height
    val sharedTransitionScope = LocalSharedTransitionScope.currentOrThrow
    val animatedContentScope = LocalAnimatedContentScope.currentOrThrow
    val prefix = rememberSaveable { Uuid.random().toHexString() }
    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val onBookmarkLongClick = {
        showBottomSheet = true
    }
    val context = LocalContext.current
    with(sharedTransitionScope) {
        val shape = 10f.round
        Box(
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(key = "${prefix}-card-${illust.id}"),
                    animatedContentScope,
                    clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(10.dp))
                )
                .padding(horizontal = 5.dp)
                .padding(bottom = 5.dp)
                .throttleClick {
                    navToPictureScreen(prefix)
                }
                .shadow(4.dp, shape, clip = false)
                .background(color = MaterialTheme.colorScheme.surface, shape = shape)
                .clip(shape),
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
                        .aspectRatio(scale)
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

                    Box(
                        modifier = Modifier
                            .sharedElement(
                                rememberSharedContentState(key = "${prefix}-favorite-${illust.id}"),
                                animatedContentScope,
                                placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                            )
                            .minimumInteractiveComponentSize()
                            .throttleClick(
                                role = Role.Button,
                                indication = ripple(bounded = false, radius = 20.dp),
                                onLongClick = onBookmarkLongClick
                            ) {
                                onBookmarkClick(Restrict.PUBLIC, null)
                            },
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
        illust = illust,
        bottomSheetState = bottomSheetState,
        onBookmarkClick = onBookmarkClick,
        isBookmarked = isBookmarked
    )
}