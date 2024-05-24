package com.mrl.pixiv.common_ui.item

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FileCopy
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mrl.pixiv.common.middleware.bookmark.BookmarkAction
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.common.ui.LocalAnimatedContentScope
import com.mrl.pixiv.common.ui.LocalSharedTransitionScope
import com.mrl.pixiv.common.ui.components.m3.IconButton
import com.mrl.pixiv.common.ui.components.m3.TextField
import com.mrl.pixiv.common.ui.components.m3.transparentIndicatorColors
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.ui.lightBlue
import com.mrl.pixiv.common_ui.R
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.IllustAiType
import com.mrl.pixiv.data.Restrict
import com.mrl.pixiv.data.Type
import com.mrl.pixiv.data.illust.BookmarkDetailTag
import com.mrl.pixiv.domain.HasShowBookmarkTipUseCase
import com.mrl.pixiv.domain.SetShowBookmarkTipUseCase
import com.mrl.pixiv.domain.illust.GetIllustBookmarkDetailUseCase
import com.mrl.pixiv.util.throttleClick
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import java.util.UUID

@Composable
fun SquareIllustItem(
    illust: Illust,
    bookmarkState: BookmarkState,
    dispatch: (BookmarkAction) -> Unit,
    spanCount: Int,
    horizontalPadding: Dp = 0.dp,
    paddingValues: PaddingValues = PaddingValues(1.dp),
    elevation: Dp = 5.dp,
    shouldShowTip: Boolean = false,
    navToPictureScreen: (Illust, String) -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val getIllustBookmarkDetailUseCase = koinInject<GetIllustBookmarkDetailUseCase>()
    val hasShowBookmarkTipUseCase = koinInject<HasShowBookmarkTipUseCase>()
    val setShowBookmarkTipUseCase = koinInject<SetShowBookmarkTipUseCase>()
    val isBookmarked = bookmarkState.bookmarkStatus[illust.id] ?: illust.isBookmarked
    var showPopupTip by remember { mutableStateOf(false) }
    val onBookmarkClick = { restrict: String, tags: List<String>? ->
        dispatch(
            if (isBookmarked) {
                BookmarkAction.IllustBookmarkDeleteIntent(illust.id)
            } else {
                BookmarkAction.IllustBookmarkAddIntent(illust.id, restrict, tags)
            }
        )
    }
    val prefix = rememberSaveable { UUID.randomUUID().toString() }
    val onClick = {
        navToPictureScreen(illust.copy(isBookmarked = isBookmarked), prefix)
    }
    LaunchedEffect(Unit) {
        showPopupTip = shouldShowTip && !hasShowBookmarkTipUseCase()
    }
    val animatedContentScope = LocalAnimatedContentScope.currentOrThrow
    with(LocalSharedTransitionScope.currentOrThrow) {
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .sharedBounds(
                    rememberSharedContentState(key = "${prefix}-card-${illust.id}"),
                    animatedContentScope,
                    clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(10.dp))
                )
//                .graphicsLayer(
//                    shadowElevation = with(LocalDensity.current) {
//                        elevation.toPx()
//                    },
//                    shape = MaterialTheme.shapes.medium,
//                    clip = false
//                )
                .shadow(elevation, MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.background)
                .throttleClick { onClick() }
        ) {
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val size =
                (screenWidth - horizontalPadding * 2 - 2 * spanCount * paddingValues.calculateLeftPadding(
                    LayoutDirection.Ltr
                ) - 1.dp) / spanCount

            val imageKey = "image-${illust.id}-0"
            AsyncImage(
                modifier = Modifier
                    .size(size)
                    .sharedElement(
                        rememberSharedContentState(key = "${prefix}-$imageKey"),
                        animatedVisibilityScope = LocalAnimatedContentScope.currentOrThrow,
                        placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                    )
                    .clip(MaterialTheme.shapes.medium),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(illust.imageUrls.squareMedium)
                    .allowRgb565(true)
                    .placeholderMemoryCacheKey(imageKey)
                    .memoryCacheKey(imageKey)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

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
            Box(
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                IconButton(
                    onClick = { onBookmarkClick(Restrict.PUBLIC, null) },
                    modifier = Modifier.sharedElement(
                        rememberSharedContentState(key = "${prefix}-favorite-${illust.id}"),
                        animatedContentScope,
                        placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                    ),
                    onLongClick = { showBottomSheet = true },
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "",
                        modifier = Modifier.size(24.dp),
                        tint = if (isBookmarked) Color.Red else Color.Gray
                    )
                }
                if (showPopupTip) {
                    LaunchedEffect(Unit) {
                        setShowBookmarkTipUseCase(true)
                        delay(3000)
                        showPopupTip = false
                    }
                    Popup(
                        alignment = Alignment.TopCenter,
                        offset = IntOffset(x = 0, y = -100)
                    ) {
                        Text(
                            text = stringResource(R.string.long_click_to_edit_favorite),
                            modifier = Modifier
                                .background(lightBlue, MaterialTheme.shapes.small)
                                .padding(8.dp)
                        )
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
        isBookmarked = isBookmarked,
        dispatch = dispatch
    )
}

@Composable
fun BottomBookmarkSheet(
    showBottomSheet: Boolean,
    hideBottomSheet: () -> Unit,
    getIllustBookmarkDetailUseCase: GetIllustBookmarkDetailUseCase,
    illust: Illust,
    bottomSheetState: SheetState,
    onBookmarkClick: (String, List<String>?) -> Unit,
    isBookmarked: Boolean,
    dispatch: (BookmarkAction) -> Unit
) {
    if (showBottomSheet) {
        var publicSwitch by remember { mutableStateOf(true) }
        val illustBookmarkDetailTags = remember { mutableStateListOf<BookmarkDetailTag>() }
        LaunchedEffect(Unit) {
            getIllustBookmarkDetailUseCase(illust.id) {
                publicSwitch = it.bookmarkDetail.restrict == Restrict.PUBLIC
                illustBookmarkDetailTags.clear()
                illustBookmarkDetailTags.addAll(it.bookmarkDetail.tags)
            }
        }
        ModalBottomSheet(
            onDismissRequest = hideBottomSheet,
            modifier = Modifier
                .imePadding(),
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            val allTags = remember(illustBookmarkDetailTags.size) {
                illustBookmarkDetailTags.map { it.name to it.isRegistered }.toMutableStateList()
            }
            val selectedTagsIndex = allTags.indices.filter { allTags[it].second }
            var inputTag by remember { mutableStateOf(TextFieldValue()) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            onBookmarkClick(
                                if (publicSwitch) Restrict.PUBLIC else Restrict.PRIVATE,
                                selectedTagsIndex.map { allTags[it].first }
                            )
                            hideBottomSheet()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isBookmarked) Color.Red else Color.Gray
                        )
                    }
                    if (isBookmarked) {
                        TextButton(
                            onClick = {
                                dispatch(
                                    BookmarkAction.IllustBookmarkAddIntent(
                                        illust.id,
                                        if (publicSwitch) Restrict.PUBLIC else Restrict.PRIVATE,
                                        selectedTagsIndex.map { allTags[it].first }
                                    )
                                )
                                hideBottomSheet()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.filledTonalButtonColors().copy(
                                containerColor = lightBlue
                            )
                        ) {
                            Text(text = stringResource(R.string.save))
                        }
                    }
                }
            }
            Text(
                text = if (isBookmarked) stringResource(R.string.edit_favorite) else stringResource(
                    R.string.add_to_favorite
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (publicSwitch) stringResource(R.string.word_public)
                    else stringResource(R.string.word_private)
                )
                Switch(checked = publicSwitch, onCheckedChange = { publicSwitch = it })
            }
            Row(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
//                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.bookmark_tags),
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = "${selectedTagsIndex.size} / 10",
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputTag,
                    onValueChange = { inputTag = it },
                    modifier = Modifier.weight(1f),
                    enabled = selectedTagsIndex.size < 10,
                    placeholder = { Text(text = stringResource(R.string.add_tags)) },
                    shape = MaterialTheme.shapes.small,
                    colors = transparentIndicatorColors
                )
                IconButton(
                    onClick = {
                        handleInputTag(inputTag, allTags)
                        inputTag = inputTag.copy(text = "")
                    },
                    modifier = Modifier
                ) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                itemsIndexed(allTags) { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.first,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Checkbox(
                            checked = item.second,
                            onCheckedChange = {
                                allTags[index] = item.first to it
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun handleInputTag(
    inputTag: TextFieldValue,
    allTags: SnapshotStateList<Pair<String, Boolean>>,
) {
    val tagText = inputTag.text.trim()
    if (tagText.isNotEmpty()) {
        // 检查是否已存在于illustBookmarkDetailTags中
        val existingTagIndex = allTags.indexOfFirst { it.first == tagText }
        if (existingTagIndex != -1) {
            // 如果存在，移动到首位
            val existingTag = allTags[existingTagIndex]
            allTags.remove(existingTag)
            allTags.add(0, existingTag)
        } else {
            // 如果不存在，添加到首位
            allTags.add(0, tagText to true)
        }
    }
}