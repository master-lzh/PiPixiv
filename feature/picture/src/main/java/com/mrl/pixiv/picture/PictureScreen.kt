package com.mrl.pixiv.picture

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import coil.Coil
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mrl.pixiv.common.coroutine.launchNetwork
import com.mrl.pixiv.common.lifecycle.OnLifecycle
import com.mrl.pixiv.common.middleware.bookmark.BookmarkAction
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.common.middleware.bookmark.BookmarkViewModel
import com.mrl.pixiv.common.middleware.follow.FollowAction
import com.mrl.pixiv.common.middleware.follow.FollowState
import com.mrl.pixiv.common.middleware.follow.FollowViewModel
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.common.ui.components.UserAvatar
import com.mrl.pixiv.common.ui.components.m3.Surface
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.ui.deepBlue
import com.mrl.pixiv.common_ui.item.SquareIllustItem
import com.mrl.pixiv.common_ui.util.navigateToOtherProfileDetailScreen
import com.mrl.pixiv.common_ui.util.navigateToOutsideSearchResultScreen
import com.mrl.pixiv.common_ui.util.navigateToPictureScreen
import com.mrl.pixiv.common_ui.util.popBackToMainScreen
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Type
import com.mrl.pixiv.picture.components.UgoiraPlayer
import com.mrl.pixiv.picture.viewmodel.PictureAction
import com.mrl.pixiv.picture.viewmodel.PictureDeeplinkViewModel
import com.mrl.pixiv.picture.viewmodel.PictureState
import com.mrl.pixiv.picture.viewmodel.PictureViewModel
import com.mrl.pixiv.util.AppUtil
import com.mrl.pixiv.util.DOWNLOAD_DIR
import com.mrl.pixiv.util.OnScrollToBottom
import com.mrl.pixiv.util.PictureType
import com.mrl.pixiv.util.calculateImageSize
import com.mrl.pixiv.util.convertUtcStringToLocalDateTime
import com.mrl.pixiv.util.isEven
import com.mrl.pixiv.util.isFileExists
import com.mrl.pixiv.util.joinPaths
import com.mrl.pixiv.util.queryParams
import com.mrl.pixiv.util.saveToAlbum
import com.mrl.pixiv.util.throttleClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.io.File


@Composable
fun PictureScreen(
    modifier: Modifier = Modifier,
    illust: Illust,
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
    pictureViewModel: PictureViewModel = koinViewModel { parametersOf(illust) },
    bookmarkViewModel: BookmarkViewModel,
    followViewModel: FollowViewModel,
) {
    OnLifecycle(onLifecycle = pictureViewModel::onCreate)
    PictureScreen(
        modifier = modifier,
        state = pictureViewModel.state,
        bookmarkState = bookmarkViewModel.state,
        followState = followViewModel.state,
        illust = illust,
        navToPictureScreen = navHostController::navigateToPictureScreen,
        popBackStack = navHostController::popBackStack,
        dispatch = pictureViewModel::dispatch,
        bookmarkDispatch = bookmarkViewModel::dispatch,
        followDispatch = followViewModel::dispatch,
        navToSearchResultScreen = navHostController::navigateToOutsideSearchResultScreen,
        popBackToHomeScreen = navHostController::popBackToMainScreen,
        navToUserDetailScreen = navHostController::navigateToOtherProfileDetailScreen,
    )
}

@Composable
fun PictureDeeplinkScreen(
    modifier: Modifier = Modifier,
    illustId: Long,
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
    pictureViewModel: PictureDeeplinkViewModel = koinViewModel { parametersOf(illustId) },
    bookmarkViewModel: BookmarkViewModel,
    followViewModel: FollowViewModel,
) {
    val illust = pictureViewModel.state.illust
    if (illust != null) {
        PictureScreen(
            modifier = modifier,
            state = pictureViewModel.state,
            bookmarkState = bookmarkViewModel.state,
            followState = followViewModel.state,
            illust = illust,
            navToPictureScreen = navHostController::navigateToPictureScreen,
            popBackStack = navHostController::popBackStack,
            dispatch = pictureViewModel::dispatch,
            bookmarkDispatch = bookmarkViewModel::dispatch,
            followDispatch = followViewModel::dispatch,
            navToSearchResultScreen = navHostController::navigateToOutsideSearchResultScreen,
            popBackToHomeScreen = navHostController::popBackToMainScreen,
            navToUserDetailScreen = navHostController::navigateToOtherProfileDetailScreen,
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


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
internal fun PictureScreen(
    modifier: Modifier = Modifier,
    state: PictureState,
    bookmarkState: BookmarkState,
    followState: FollowState,
    illust: Illust,
    navToPictureScreen: (Illust) -> Unit = {},
    popBackStack: () -> Unit = {},
    dispatch: (PictureAction) -> Unit = {},
    bookmarkDispatch: (BookmarkAction) -> Unit = {},
    followDispatch: (FollowAction) -> Unit = {},
    navToSearchResultScreen: (String) -> Unit = {},
    popBackToHomeScreen: () -> Unit = {},
    navToUserDetailScreen: (Long) -> Unit = {},
) {
    val context = LocalContext.current
    val (relatedSpanCount, userSpanCount) = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> Pair(2, 3)
        Configuration.ORIENTATION_LANDSCAPE -> Pair(4, 6)
        else -> Pair(2, 3)
    }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val shareLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 处理分享结果
            if (result.resultCode == Activity.RESULT_OK) {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.sharing_success))
                }
            } else {
                // 分享失败或取消
            }
        }
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val currPage =
        remember {
            derivedStateOf {
                minOf(
                    lazyStaggeredGridState.firstVisibleItemIndex,
                    illust.pageCount - 1
                )
            }
        }
    val isBarVisible by remember { derivedStateOf { lazyStaggeredGridState.firstVisibleItemIndex <= illust.pageCount } }
    val isScrollToBottom = rememberSaveable { mutableStateOf(false) }
    val isScrollToRelatedBottom = rememberSaveable { mutableStateOf(false) }

    val isFollowed = followState.followStatus[illust.user.id] ?: false
    val placeholder = rememberVectorPainter(Icons.Rounded.Refresh)
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    var currLongClickPic by rememberSaveable { mutableStateOf(Pair(0, "")) }
    var currLongClickPicSize by rememberSaveable { mutableFloatStateOf(0f) }
    var loading by rememberSaveable { mutableStateOf(false) }
    val readMediaImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberMultiplePermissionsState(permissions = listOf(READ_MEDIA_IMAGES))
    } else {
        rememberMultiplePermissionsState(permissions = listOf(READ_EXTERNAL_STORAGE))
    }

    LaunchedEffect(currLongClickPic.second) {
        if (currLongClickPic.second.isNotEmpty()) {
            launchNetwork {
                currLongClickPicSize = calculateImageSize(currLongClickPic.second)
            }
        }
    }

    var currentLoadingItem by rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(state.illustRelated.size) {
        currentLoadingItem = if (state.illustRelated.size.isEven()) {
            4
        } else {
            5
        }
    }

    Screen(
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
                                    val shareIntent = createShareIntent(
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
                    .throttleClick {
                        if (bookmarkState.bookmarkStatus[illust.id] == true) {
                            bookmarkDispatch(
                                BookmarkAction.IllustBookmarkDeleteIntent(
                                    illust.id
                                )
                            )
                        } else {
                            bookmarkDispatch(BookmarkAction.IllustBookmarkAddIntent(illust.id))
                        }
                    }
                    .shadow(5.dp, CircleShape)
                    .background(
                        if (!isSystemInDarkTheme()) Color.White else Color.DarkGray,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .padding(10.dp)

            ) {
                val isBookmarked = bookmarkState.bookmarkStatus[illust.id] ?: illust.isBookmarked
                Icon(
                    imageVector = if (isBookmarked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isBookmarked) Color.Red else LocalContentColor.current,
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                )
            }
        }
    ) {
        LazyVerticalStaggeredGrid(
            state = lazyStaggeredGridState,
            columns = StaggeredGridCells.Fixed(relatedSpanCount),
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (illust.type == Type.Ugoira) {
                item(span = StaggeredGridItemSpan.FullLine, key = "ugoira") {
                    UgoiraPlayer(
                        images = state.ugoiraImages,
                        placeholder = placeholder
                    )
                }
            } else {
                items(
                    illust.pageCount,
                    key = { "${illust.id}_$it" },
                    span = { StaggeredGridItemSpan.FullLine }
                ) { index ->
                    if (illust.pageCount > 1) {
                        illust.metaPages?.get(index)?.let {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(it.imageUrls?.large)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .throttleClick(
                                        onLongClick = {
                                            currLongClickPic =
                                                Pair(index, it.imageUrls?.original ?: "")
                                            openBottomSheet = true
                                        }
                                    ),
                                contentScale = ContentScale.FillWidth,
                                placeholder = placeholder,
                            )
                        }
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(illust.imageUrls.large)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .throttleClick(
                                    onLongClick = {
                                        currLongClickPic =
                                            Pair(0, illust.metaSinglePage.originalImageURL)
                                        openBottomSheet = true
                                    }
                                ),
                            contentScale = ContentScale.FillWidth,
                            placeholder = placeholder,
                        )
                    }
                }
            }
            item(span = StaggeredGridItemSpan.FullLine, key = "illust_title") {
                if (isScrollToBottom.value) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 10.dp)
                    ) {
                        UserAvatar(
                            url = illust.user.profileImageUrls.medium,
                            onClick = {
                                navToUserDetailScreen(illust.user.id)
                            },
                            modifier = Modifier
                                .padding(start = 20.dp)
                                .size(30.dp)
                                .align(Alignment.CenterVertically),
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
                                style = TextStyle(
                                    fontSize = 12.sp,
                                ),
                                maxLines = 1,
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )
                }
            }
            item(span = StaggeredGridItemSpan.FullLine, key = "illust_data") {
                Row(
                    Modifier.padding(top = 10.dp)
                ) {
                    Text(
                        text = convertUtcStringToLocalDateTime(illust.createDate),
                        modifier = Modifier.padding(start = 20.dp),
                        style = TextStyle(fontSize = 12.sp),
                    )
                    Text(
                        text = illust.totalView.toString() + " ${stringResource(R.string.viewed)}",
                        Modifier.padding(start = 10.dp),
                        style = TextStyle(fontSize = 12.sp),
                    )
                    Text(
                        text = illust.totalBookmarks.toString() + " ${stringResource(R.string.liked)}",
                        Modifier.padding(start = 10.dp),
                        style = TextStyle(fontSize = 12.sp),
                    )
                }
            }
            // tag
            item(span = StaggeredGridItemSpan.FullLine, key = "illust_tags") {
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
            item(span = StaggeredGridItemSpan.FullLine, key = "illust_divider_1") {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(top = 50.dp)
                )
            }
            item(span = StaggeredGridItemSpan.FullLine, key = "illust_author") {
                //作者头像、名字、关注按钮
                Row(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(top = 10.dp)
                ) {
                    UserAvatar(
                        url = illust.user.profileImageUrls.medium,
                        onClick = {
                            navToUserDetailScreen(illust.user.id)
                        },
                        modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.CenterVertically),
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = illust.user.name,
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
                                    followDispatch(FollowAction.UnFollowUser(illust.user.id))
                                },
                            text = stringResource(R.string.followed),
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
                                    followDispatch(FollowAction.FollowUser(illust.user.id))
                                },
                            text = stringResource(R.string.follow),
                            style = TextStyle(
                                color = deepBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                }
            }
            item(span = StaggeredGridItemSpan.FullLine, key = "illust_author_other_works") {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(top = 10.dp)
                ) {
                    state.userIllusts.subList(0, minOf(userSpanCount, state.userIllusts.size))
                        .forEach {
                            SquareIllustItem(
                                illust = it,
                                bookmarkState = bookmarkState,
                                dispatch = bookmarkDispatch,
                                spanCount = minOf(userSpanCount, state.userIllusts.size),
                                horizontalPadding = 15.dp,
                                paddingValues = PaddingValues(2.dp),
                                elevation = 5.dp,
                                navToPictureScreen = navToPictureScreen
                            )
                        }
                }
            }
            item(span = StaggeredGridItemSpan.FullLine, key = "illust_related_title") {
                //相关作品文字，显示在中间
                Text(
                    text = stringResource(R.string.related_artworks),
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
                state.illustRelated,
                key = { "${illust.id}_related_${it.id}" },
                contentType = { "related_illusts" }
            ) {
                // 相关作品
                // 由于不能在LazyColumn中嵌套LazyColumn，所以这里拆分成每个Row两张图片
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    SquareIllustItem(
                        illust = it,
                        bookmarkState = bookmarkState,
                        dispatch = bookmarkDispatch,
                        spanCount = relatedSpanCount,
                        paddingValues = PaddingValues(5.dp),
                        elevation = 5.dp,
                        navToPictureScreen = navToPictureScreen
                    )
                }
            }

            items(currentLoadingItem, key = { "loading_$it" }) {
                val size =
                    (LocalConfiguration.current.screenWidthDp.dp - 2 * relatedSpanCount * PaddingValues(
                        5.dp
                    ).calculateLeftPadding(
                        LayoutDirection.Ltr
                    ) - 1.dp) / relatedSpanCount
                Surface(
                    Modifier
                        .padding(5.dp)
                        .size(size),
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 4.dp,
                    propagateMinConstraints = false,
                ) {

                }
            }

            item(key = "spacer") {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
        lazyStaggeredGridState.OnScrollToBottom(isScrollToBottom, illust.pageCount, illust.id)
        lazyStaggeredGridState.OnScrollToBottom(loadingItemCount = currentLoadingItem) {
            isScrollToRelatedBottom.value = true
            state.nextUrl.queryParams.takeIf { it.isNotEmpty() }?.let {
                dispatch(PictureAction.LoadMoreIllustRelatedIntent(state.nextUrl.queryParams))
            }
        }
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val picInfo = createRef()
            AnimatedVisibility(
                visible = !isScrollToBottom.value,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .constrainAs(picInfo) {
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = 10.dp)
                ) {
                    UserAvatar(
                        url = illust.user.profileImageUrls.medium,
                        onClick = {
                            navToUserDetailScreen(illust.user.id)
                        },
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .size(20.dp)
                            .align(Alignment.CenterVertically),
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
                            style = TextStyle(
                                fontSize = 12.sp,
                            ),
                            maxLines = 1,
                        )
                    }
                }
            }
        }
        if (openBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    openBottomSheet = false
                },
                modifier = Modifier
                    .heightIn(LocalConfiguration.current.screenHeightDp.dp / 2),
                sheetState = bottomSheetState,
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .throttleClick {
                                loading = true
                                // 下载原始图片
                                dispatch(
                                    PictureAction.DownloadIllust(
                                        illust.id,
                                        currLongClickPic.first,
                                        currLongClickPic.second
                                    ) {
                                        loading = false
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                if (it) context.getString(R.string.download_success)
                                                else context.getString(R.string.download_failed)
                                            )
                                        }
                                    })
                                openBottomSheet = false
                            }
                            .padding(vertical = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Rounded.Download, contentDescription = null)
                        Text(
                            text = stringResource(
                                R.string.download_with_size,
                                currLongClickPicSize
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
                                    scope.launch(Dispatchers.IO) {
                                        loading = true
                                        if (createShareImage(
                                                currLongClickPic,
                                                illust,
                                                shareLauncher
                                            )
                                        ) return@launch
                                        loading = false
                                        currLongClickPic = Pair(0, "")
                                    }
                                    openBottomSheet = false
                                }
                            }
                            .padding(vertical = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Rounded.Share, contentDescription = null)
                        Text(
                            text = stringResource(R.string.share),
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }
        }
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

private suspend fun createShareImage(
    currLongClickPic: Pair<Int, String>,
    illust: Illust,
    shareLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
): Boolean {
    val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        .absolutePath.let {
            File(
                joinPaths(
                    it,
                    DOWNLOAD_DIR,
                    "${illust.id}_${currLongClickPic.first}${PictureType.PNG.extension}"
                )
            )
        }
    if (!isFileExists(file)) {
        val imageLoader = Coil.imageLoader(AppUtil.appContext)
        val request = ImageRequest
            .Builder(AppUtil.appContext)
            .data(currLongClickPic.second)
            .build()
        val result = imageLoader.execute(request)
        result.drawable
            ?.toBitmap()
            ?.saveToAlbum(file.nameWithoutExtension, PictureType.PNG)
            ?: return true
    }
    val uri = FileProvider.getUriForFile(
        AppUtil.appContext,
        "${AppUtil.appContext.packageName}.fileprovider",
        file
    )
    // 分享图片
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "image/*"
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    shareLauncher.launch(intent)
    return false
}

private fun createShareIntent(text: String): Intent {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, text)
    return Intent.createChooser(shareIntent, "Share")
}

@Composable
private fun LazyStaggeredGridState.OnScrollToBottom(
    isScrollToBottom: MutableState<Boolean>,
    pageCount: Int,
    id: Long
) {
    val isToBottom by remember {
        derivedStateOf {
            val lastVisibleItem =
                layoutInfo.visibleItemsInfo.find { it.key == "${id}_${pageCount - 1}" }
//            layoutInfo.visibleItemsInfo.forEachIndexed { i, it ->
//                Log.d(
//                    "TAG",
//                    "OnScrollToBottom: ${it.index} ${
//                        it.let { "${it.offset} ${it.size} ${layoutInfo.viewportStartOffset} ${layoutInfo.viewportEndOffset}" }
//                    }"
//                )
//            }


            if (lastVisibleItem != null) {
                return@derivedStateOf lastVisibleItem.index == pageCount - 1
                        && lastVisibleItem.let { it.offset.y + it.size.height } <= layoutInfo.let { it.viewportEndOffset - it.viewportStartOffset }
            } else {
                return@derivedStateOf false
            }
        }
    }
    LaunchedEffect(isToBottom) {
        Log.d("TAG", "OnScrollToBottom: $isToBottom")
        isScrollToBottom.value = isToBottom
        Log.d("TAG", "OnScrollToBottom: $isScrollToBottom")
    }
}