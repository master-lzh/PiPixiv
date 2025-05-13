package com.mrl.pixiv.picture

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.data.Restrict
import com.mrl.pixiv.common.data.Type
import com.mrl.pixiv.common.kts.spaceBy
import com.mrl.pixiv.common.ui.*
import com.mrl.pixiv.common.ui.components.TextSnackbar
import com.mrl.pixiv.common.ui.components.UserAvatar
import com.mrl.pixiv.common.ui.item.SquareIllustItem
import com.mrl.pixiv.common.util.*
import com.mrl.pixiv.common.util.AppUtil.getString
import com.mrl.pixiv.common.viewmodel.SideEffect
import com.mrl.pixiv.common.viewmodel.asState
import com.mrl.pixiv.common.viewmodel.bookmark.BookmarkState
import com.mrl.pixiv.common.viewmodel.bookmark.requireBookmarkState
import com.mrl.pixiv.common.viewmodel.follow.FollowState
import com.mrl.pixiv.common.viewmodel.follow.requireFollowState
import com.mrl.pixiv.picture.components.UgoiraPlayer
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Composable
fun PictureDeeplinkScreen(
    modifier: Modifier = Modifier,
    illustId: Long,
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
    pictureViewModel: PictureViewModel = koinViewModel { parametersOf(null, illustId) },
) {
    val state = pictureViewModel.asState()
    val illust = state.illust
    val sideEffect by pictureViewModel.sideEffect
        .filterIsInstance<SideEffect.Error>()
        .collectAsStateWithLifecycle(null)
    val exception = sideEffect?.throwable
    if (illust != null) {
        PictureScreen(
            pictureViewModel = pictureViewModel,
            exception = exception,
            relatedIllusts = pictureViewModel.relatedIllusts.collectAsLazyPagingItems(),
            illust = illust,
            modifier = modifier,
            navToPictureScreen = navHostController::navigateToPictureScreen,
            popBackStack = navHostController::popBackStack,
            dispatch = pictureViewModel::dispatch,
            navToSearchResultScreen = navHostController::navigateToOutsideSearchResultScreen,
            popBackToHomeScreen = navHostController::popBackToMainScreen,
            navToUserDetailScreen = navHostController::navigateToProfileDetailScreen,
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
internal fun PictureScreen(
    illust: Illust,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
    pictureViewModel: PictureViewModel = koinViewModel { parametersOf(illust, null) },
) {
    val sideEffect by pictureViewModel.sideEffect
        .filterIsInstance<SideEffect.Error>()
        .collectAsStateWithLifecycle(null)
    val exception = sideEffect?.throwable
    PictureScreen(
        pictureViewModel = pictureViewModel,
        exception = exception,
        relatedIllusts = pictureViewModel.relatedIllusts.collectAsLazyPagingItems(),
        illust = illust,
        modifier = modifier,
        navToPictureScreen = navHostController::navigateToPictureScreen,
        popBackStack = onBack,
        dispatch = pictureViewModel::dispatch,
        navToSearchResultScreen = navHostController::navigateToOutsideSearchResultScreen,
        popBackToHomeScreen = navHostController::popBackToMainScreen,
        navToUserDetailScreen = navHostController::navigateToProfileDetailScreen,
    )
}

private const val KEY_UGOIRA = "ugoira"
private const val KEY_ILLUST_TITLE = "illust_title"
private const val KEY_ILLUST_DATA = "illust_data"
private const val KEY_ILLUST_TAGS = "illust_tags"
private const val KEY_ILLUST_DIVIDER_1 = "illust_divider_1"
private const val KEY_ILLUST_AUTHOR = "illust_author"
private const val KEY_ILLUST_AUTHOR_OTHER_WORKS = "illust_author_other_works"
private const val KEY_ILLUST_RELATED_TITLE = "illust_related_title"
private const val KEY_SPACER = "spacer"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun PictureScreen(
    pictureViewModel: PictureViewModel,
    exception: Throwable?,
    relatedIllusts: LazyPagingItems<Illust>,
    illust: Illust,
    modifier: Modifier = Modifier,
    navToPictureScreen: NavigateToHorizontalPictureScreen = { _, _, _ -> },
    popBackStack: () -> Unit = {},
    dispatch: (PictureAction) -> Unit = {},
    navToSearchResultScreen: (String) -> Unit = {},
    popBackToHomeScreen: () -> Unit = {},
    navToUserDetailScreen: (Long) -> Unit = {},
) {
    val state = pictureViewModel.asState()
    val context = LocalContext.current
    val (relatedSpanCount, userSpanCount) = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> Pair(2, 3)
        Configuration.ORIENTATION_LANDSCAPE -> Pair(4, 6)
        else -> Pair(2, 3)
    }
    val relatedRowCount = if (relatedIllusts.itemCount % relatedSpanCount == 0) {
        relatedIllusts.itemCount / relatedSpanCount
    } else {
        relatedIllusts.itemCount / relatedSpanCount + 1
    }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val shareLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 处理分享结果
            if (result.resultCode == Activity.RESULT_OK) {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(RString.sharing_success))
                }
            } else {
                // 分享失败或取消
            }
        }
    val lazyListState = rememberLazyListState()
    val currPage =
        remember {
            derivedStateOf {
                minOf(
                    lazyListState.firstVisibleItemIndex,
                    illust.pageCount - 1
                )
            }
        }
    val isBarVisible by remember { derivedStateOf { lazyListState.firstVisibleItemIndex <= illust.pageCount } }
    val isScrollToBottom = lazyListState.onScrollToBottom(illust.pageCount, illust.id)

    val isBookmarked = requireBookmarkState[illust.id] ?: illust.isBookmarked
    val onBookmarkClick = { restrict: String, tags: List<String>? ->
        if (isBookmarked) {
            BookmarkState.deleteBookmarkIllust(illust.id)
        } else {
            BookmarkState.bookmarkIllust(illust.id, restrict, tags)
        }
    }
    val isFollowed = requireFollowState[illust.user.id] == true
    val placeholder = rememberVectorPainter(Icons.Rounded.Refresh)
    val bottomSheetState = rememberModalBottomSheetState()
    val readMediaImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberMultiplePermissionsState(permissions = listOf(READ_MEDIA_IMAGES))
    } else {
        rememberMultiplePermissionsState(permissions = listOf(READ_EXTERNAL_STORAGE))
    }

    LaunchedEffect(exception) {
        if (exception != null) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    exception.message ?: getString(RString.unknown_error)
                )
            }
        }
    }

    val prefix = LocalSharedKeyPrefix.current
    val sharedTransitionScope = LocalSharedTransitionScope.currentOrThrow
    val animatedContentScope = LocalAnimatedContentScope.currentOrThrow
    with(sharedTransitionScope) {
        Scaffold(
            modifier = modifier.sharedBounds(
                rememberSharedContentState(key = "${prefix}-card-${illust.id}"),
                animatedContentScope,
                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(10.dp))
            ),
            topBar = {
                TopAppBar(
                    title = {},
                    actions = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterStart),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.throttleClick { popBackStack() },
                                )
                                Icon(
                                    imageVector = Icons.Rounded.Home,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(start = 15.dp)
                                        .throttleClick { popBackToHomeScreen() }
                                )
                            }
                            // 分享按钮
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .throttleClick {
                                        val shareIntent = ShareUtil.createShareIntent(
                                            "${illust.title} | ${illust.user.name} #pixiv https://www.pixiv.net/artworks/${illust.id}"
                                        )
                                        shareLauncher.launch(shareIntent)
                                    },
                            )
                            this@TopAppBar.AnimatedVisibility(
                                modifier = Modifier.align(Alignment.Center),
                                visible = isBarVisible,
                                enter = fadeIn(),
                                exit = fadeOut(),
                            ) {
                                Text(
                                    text = "${currPage.value + 1}/${illust.pageCount}",
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors()
                        .copy(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                Box(
                    Modifier
                        .sharedElement(
                            rememberSharedContentState(key = "${prefix}-favorite-${illust.id}"),
                            animatedContentScope,
                            placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                        )
                        .throttleClick {
                            onBookmarkClick(Restrict.PUBLIC, null)
                        }
                        .shadow(5.dp, CircleShape)
                        .background(
                            if (!isSystemInDarkTheme()) Color.White else Color.DarkGray,
                        )
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isBookmarked) Color.Red else LocalContentColor.current,
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState) {
                    TextSnackbar(
                        text = it.visuals.message,
                    )
                }
            },
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize()
            ) {
                with(sharedTransitionScope) {
                    if (illust.type == Type.Ugoira) {
                        item(key = KEY_UGOIRA) {
                            UgoiraPlayer(
                                images = state.ugoiraImages,
                                placeholder = placeholder
                            )
                        }
                    } else {
                        items(
                            illust.pageCount,
                            key = { "${illust.id}_$it" },
                        ) { index ->
                            val firstImageKey = "image-${illust.id}-0"
                            if (illust.pageCount > 1) {
                                illust.metaPages?.get(index)?.let {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(it.imageUrls?.medium)
                                            .placeholderMemoryCacheKey("image-${illust.id}-$index")
                                            .build(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .then(
                                                if (index == 0) Modifier.sharedElement(
                                                    sharedTransitionScope.rememberSharedContentState(
                                                        key = "${prefix}-$firstImageKey"
                                                    ),
                                                    animatedVisibilityScope = animatedContentScope,
                                                    placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                                                )
                                                else Modifier
                                            )
                                            .throttleClick(
                                                onLongClick = {
                                                    dispatch(PictureAction.GetPictureInfo(index))
                                                }
                                            ),
                                        contentScale = ContentScale.FillWidth,
                                        placeholder = placeholder,
                                    )
                                }
                            } else {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(illust.imageUrls.medium)
                                        .placeholderMemoryCacheKey("image-${illust.id}-$index")
                                        .build(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(
                                            if (index == 0) Modifier.sharedElement(
                                                sharedTransitionScope.rememberSharedContentState(key = "${prefix}-$firstImageKey"),
                                                animatedVisibilityScope = animatedContentScope,
                                                placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                                            )
                                            else Modifier
                                        )
                                        .throttleClick(
                                            onLongClick = {
                                                dispatch(PictureAction.GetPictureInfo(0))
                                            }
                                        ),
                                    contentScale = ContentScale.FillWidth,
                                    placeholder = placeholder,
                                )
                            }
                        }
                    }
                }
                item(key = KEY_ILLUST_TITLE) {
                    UserInfo(
                        illust = illust,
                        navToUserDetailScreen = navToUserDetailScreen
                    )
                }
                item(key = KEY_ILLUST_DATA) {
                    Row(
                        Modifier.padding(top = 10.dp)
                    ) {
                        Text(
                            text = convertUtcStringToLocalDateTime(illust.createDate),
                            modifier = Modifier.padding(start = 20.dp),
                            style = TextStyle(fontSize = 12.sp),
                        )
                        Text(
                            text = illust.totalView.toString() + " ${stringResource(RString.viewed)}",
                            Modifier.padding(start = 10.dp),
                            style = TextStyle(fontSize = 12.sp),
                        )
                        Text(
                            text = illust.totalBookmarks.toString() + " ${stringResource(RString.liked)}",
                            Modifier.padding(start = 10.dp),
                            style = TextStyle(fontSize = 12.sp),
                        )
                    }
                }
                // tag
                item(key = KEY_ILLUST_TAGS) {
                    FlowRow(
                        Modifier.padding(start = 20.dp, top = 10.dp)
                    ) {
                        illust.tags?.forEach {
                            Text(
                                text = "#" + it.name,
                                modifier = Modifier
                                    .padding(vertical = 2.5.dp)
                                    .padding(end = 5.dp)
                                    .throttleClick {
                                        navToSearchResultScreen(it.name)
                                        dispatch(PictureAction.AddSearchHistory(it.name))
                                    },
                                style = TextStyle(fontSize = 12.sp, color = deepBlue),
                            )
                            Text(
                                text = it.translatedName,
                                modifier = Modifier
                                    .padding(vertical = 2.5.dp)
                                    .padding(end = 10.dp),
                                style = TextStyle(fontSize = 12.sp)
                            )
                        }
                    }
                }
                item(key = KEY_ILLUST_DIVIDER_1) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .padding(top = 50.dp)
                    )
                }
                item(key = KEY_ILLUST_AUTHOR) {
                    //作者头像、名字、关注按钮
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .padding(top = 10.dp)
                    ) {
                        UserAvatar(
                            url = illust.user.profileImageUrls.medium,
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.CenterVertically),
                            onClick = {
                                navToUserDetailScreen(illust.user.id)
                            },
                        )
                        Column(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = illust.user.name,
                                modifier = Modifier
                                    .sharedElement(
                                        rememberSharedContentState(key = "${prefix}-user-name-${illust.user.id}"),
                                        animatedContentScope,
                                        placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                                    )
                                    .skipToLookaheadSize(),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                ),
                            )
                            Text(
                                text = "ID: ${illust.user.id}",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                ),
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        if (isFollowed) {
                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                                    .throttleClick {
                                        FollowState.unFollowUser(illust.user.id)
                                    },
                                text = stringResource(RString.followed),
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                ),
                            )
                        } else {
                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .border(
                                        width = 1.dp,
                                        color = deepBlue,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                                    .throttleClick {
                                        FollowState.followUser(illust.user.id)
                                    },
                                text = stringResource(RString.follow),
                                style = TextStyle(
                                    color = deepBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                ),
                            )
                        }
                    }
                }
                item(key = KEY_ILLUST_AUTHOR_OTHER_WORKS) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .padding(top = 10.dp),
                        horizontalArrangement = 5f.spaceBy
                    ) {
                        val otherPrefix = rememberSaveable { Uuid.random().toHexString() }
                        CompositionLocalProvider(
                            LocalSharedKeyPrefix provides otherPrefix
                        ) {
                            val illusts = state.userIllusts.take(userSpanCount)
                            illusts.forEachIndexed { index, it ->
                                val innerIsBookmarked =
                                    requireBookmarkState[it.id] ?: it.isBookmarked
                                SquareIllustItem(
                                    illust = it,
                                    isBookmarked = innerIsBookmarked,
                                    onBookmarkClick = { restrict: String, tags: List<String>? ->
                                        if (innerIsBookmarked) {
                                            BookmarkState.deleteBookmarkIllust(it.id)
                                        } else {
                                            BookmarkState.bookmarkIllust(it.id, restrict, tags)
                                        }
                                    },
                                    navToPictureScreen = { prefix ->
                                        navToPictureScreen(illusts, index, prefix)
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
                item(key = KEY_ILLUST_RELATED_TITLE) {
                    //相关作品文字，显示在中间
                    Text(
                        text = stringResource(RString.related_artworks),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp, bottom = 10.dp),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        ),
                    )
                }
                items(
                    relatedRowCount,
                    key = { index -> "${illust.id}_related_${index}" },
                    contentType = { "related_illusts" }
                ) { rowIndex ->
                    val illustsPair = (0..relatedSpanCount - 1).mapNotNull { it ->
                        val index = rowIndex * relatedSpanCount + it
                        val illust = relatedIllusts[index] ?: return@mapNotNull null
                        Triple(
                            illust,
                            requireBookmarkState[illust.id] ?: illust.isBookmarked,
                            index
                        )
                    }
                    if (illustsPair.isEmpty()) return@items
                    // 相关作品
                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 5.dp),
                        horizontalArrangement = 5f.spaceBy
                    ) {
                        illustsPair.forEach { (illust, isBookmarked, index) ->
                            SquareIllustItem(
                                illust = illust,
                                isBookmarked = isBookmarked,
                                onBookmarkClick = { restrict: String, tags: List<String>? ->
                                    if (isBookmarked) {
                                        BookmarkState.deleteBookmarkIllust(illust.id)
                                    } else {
                                        BookmarkState.bookmarkIllust(illust.id, restrict, tags)
                                    }
                                },
                                navToPictureScreen = { prefix ->
                                    navToPictureScreen(
                                        relatedIllusts.itemSnapshotList.items,
                                        index,
                                        prefix
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shouldShowTip = index == 0
                            )
                        }
                    }
                }

                item(key = KEY_SPACER) {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AnimatedVisibility(
                    visible = !isScrollToBottom,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                ) {
                    UserInfo(
                        illust = illust,
                        navToUserDetailScreen = navToUserDetailScreen,
                    )
                }
            }
            if (state.bottomSheetState != null) {
                ModalBottomSheet(
                    onDismissRequest = {
                        pictureViewModel.closeBottomSheet()
                    },
                    modifier = Modifier
                        .heightIn(getScreenHeight() / 2),
                    sheetState = bottomSheetState,
                    containerColor = MaterialTheme.colorScheme.background,
                ) {
                    Box {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .throttleClick {
                                        // 下载原始图片
                                        dispatch(
                                            PictureAction.DownloadIllust(
                                                illust.id,
                                                state.bottomSheetState.index,
                                                state.bottomSheetState.downloadUrl
                                            )
                                        )
                                    }
                                    .padding(vertical = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Download,
                                    contentDescription = null
                                )
                                Text(
                                    text = stringResource(
                                        RString.download_with_size,
                                        state.bottomSheetState.downloadSize
                                    ),
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .throttleClick {
                                        readMediaImagePermission.launchMultiplePermissionRequest()
                                        if (readMediaImagePermission.allPermissionsGranted) {
                                            pictureViewModel.shareImage(
                                                state.bottomSheetState.index,
                                                state.bottomSheetState.downloadUrl,
                                                illust,
                                                shareLauncher
                                            )
                                        }
                                    }
                                    .padding(vertical = 10.dp)
                            ) {
                                Icon(imageVector = Icons.Rounded.Share, contentDescription = null)
                                Text(
                                    text = stringResource(RString.share),
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                            }
                        }
                        if (state.loading) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .throttleClick {},
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserInfo(
    illust: Illust,
    navToUserDetailScreen: (Long) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 20.dp, top = 10.dp, bottom = 10.dp),
    ) {
        UserAvatar(
            url = illust.user.profileImageUrls.medium,
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.CenterVertically),
            onClick = {
                navToUserDetailScreen(illust.user.id)
            },
        )
        Column(
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Text(
                text = illust.title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
            )
            Text(
                text = illust.user.name,
                modifier = Modifier,
                style = TextStyle(
                    fontSize = 12.sp,
                ),
                maxLines = 1,
            )
        }
    }
}


@Composable
private fun LazyListState.onScrollToBottom(
    pageCount: Int,
    id: Long
): Boolean {
    val isToBottom by remember {
        derivedStateOf {
            val lastVisibleItem =
                layoutInfo.visibleItemsInfo.find { it.key == "${id}_${pageCount - 1}" }

            if (lastVisibleItem != null) {
                return@derivedStateOf lastVisibleItem.index == pageCount - 1
                        && lastVisibleItem.let { it.offset + it.size } <= layoutInfo.let { it.viewportEndOffset - it.viewportStartOffset }
            } else {
                return@derivedStateOf false
            }
        }
    }
    return isToBottom
}