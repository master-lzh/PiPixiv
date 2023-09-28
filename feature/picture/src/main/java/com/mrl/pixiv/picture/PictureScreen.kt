@file:OptIn(ExperimentalEncodingApi::class)

package com.mrl.pixiv.picture

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mrl.pixiv.common.compose.OnLifecycle
import com.mrl.pixiv.common.middleware.bookmark.BookmarkAction
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.common.middleware.bookmark.BookmarkViewModel
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.ui.components.UserAvatar
import com.mrl.pixiv.common_ui.item.SquareIllustItem
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.picture.viewmodel.PictureAction
import com.mrl.pixiv.picture.viewmodel.PictureState
import com.mrl.pixiv.picture.viewmodel.PictureViewModel
import com.mrl.pixiv.util.click
import com.mrl.pixiv.util.convertUtcStringToLocalDateTime
import com.mrl.pixiv.util.queryParams
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val USER_ILLUSTS_COUNT = 3

@Composable
fun PictureScreen(
    modifier: Modifier = Modifier,
    illust: Illust,
    navHostController: NavHostController,
    viewModel: PictureViewModel = koinViewModel(),
    bookmarkViewModel: BookmarkViewModel = koinViewModel()
) {
    OnLifecycle(onLifecycle = viewModel::onCreate, lifecycleEvent = Lifecycle.Event.ON_CREATE)
    LaunchedEffect(Unit) {
        bookmarkViewModel.dispatch(BookmarkAction.UpdateState(state = BookmarkState(isBookmark = illust.isBookmarked)))
    }
    PictureScreen(
        modifier = modifier,
        illust = illust,
        state = viewModel.state,
        bookmarkState = bookmarkViewModel.state,
        navHostController = navHostController,
        viewModel = viewModel,
        bookmarkViewModel = bookmarkViewModel,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
internal fun PictureScreen(
    modifier: Modifier = Modifier,
    state: PictureState,
    bookmarkState: BookmarkState,
    illust: Illust,
    navHostController: NavHostController = rememberNavController(),
    viewModel: PictureViewModel,
    bookmarkViewModel: BookmarkViewModel,
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val shareLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 处理分享结果
            if (result.resultCode == Activity.RESULT_OK) {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar("分享成功")
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
    val isScrollToBottom = remember { mutableStateOf(false) }
    val isScrollToRelatedBottom = remember { mutableStateOf(false) }
    var isBookmarked by remember { mutableStateOf(illust.isBookmarked) }
    val placeholder = rememberVectorPainter(Icons.Rounded.Refresh)
    LaunchedEffect(Unit) {
        viewModel.dispatch(PictureAction.GetUserIllustsIntent(illust.user.id))
    }
    LaunchedEffect(bookmarkState) {
        isBookmarked = bookmarkState.isBookmark
    }
    LaunchedEffect(Unit) {
        viewModel.dispatch(PictureAction.GetIllustRelatedIntent(illust.id))
    }
    LaunchedEffect(isScrollToRelatedBottom.value) {
        if (isScrollToRelatedBottom.value) {
            state.nextUrl.queryParams.takeIf { it.isNotEmpty() }?.let {
                viewModel.dispatch(PictureAction.LoadMoreIllustRelatedIntent(it))
            }
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            Column {
                Spacer(
                    Modifier
                        .background(Color.Black)
                        .windowInsetsTopHeight(WindowInsets.statusBars) // Match the height of the status bar
                        .fillMaxWidth()
                )
            }
        },
        floatingActionButton = {
            Box(
                Modifier
                    .click {
                        if (isBookmarked) {
                            bookmarkViewModel.dispatch(
                                BookmarkAction.IllustBookmarkDeleteIntent(
                                    illust.id
                                )
                            )
                        } else {
                            bookmarkViewModel.dispatch(BookmarkAction.IllustBookmarkAddIntent(illust.id))
                        }
                    }
                    .shadow(5.dp, CircleShape)
                    .background(
                        if (MaterialTheme.colors.isLight) Color.White else Color.DarkGray,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .padding(10.dp)

            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isBookmarked) Color.Red else LocalContentColor.current.copy(
                        alpha = LocalContentAlpha.current
                    ),
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                )
            }
        }
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(illust.pageCount, key = { "${illust.id}_$it" }) {
                if (illust.pageCount > 1) {
                    illust.metaPages?.get(it)?.let {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(it.imageUrls?.original)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth,
                            placeholder = placeholder,
                        )
                    }
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(illust.metaSinglePage.originalImageURL)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth,
                        placeholder = placeholder,
                    )
                }
            }
            item {
                if (isScrollToBottom.value) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background)
                            .padding(vertical = 10.dp)
                    ) {
                        UserAvatar(
                            url = illust.user.profileImageUrls.medium,
                            size = 20.dp,
                            modifier = Modifier
                                .padding(start = 20.dp)
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
            item {
                Row(
                    Modifier.padding(top = 10.dp)
                ) {
                    Text(
                        text = convertUtcStringToLocalDateTime(illust.createDate),
                        modifier = Modifier.padding(start = 20.dp),
                        style = TextStyle(fontSize = 12.sp),
                    )
                    Text(
                        text = illust.totalView.toString() + " 阅读",
                        Modifier.padding(start = 10.dp),
                        style = TextStyle(fontSize = 12.sp),
                    )
                    Text(
                        text = illust.totalBookmarks.toString() + " 收藏！",
                        Modifier.padding(start = 10.dp),
                        style = TextStyle(fontSize = 12.sp),
                    )
                }
            }
            // tag
            item {
                FlowRow(
                    Modifier.padding(start = 20.dp, top = 10.dp)
                ) {
                    illust.tags?.forEach {
                        //todo 点击tag搜索
                        Text(
                            text = "#" + it.name,
                            modifier = Modifier
                                .padding(vertical = 2.5.dp)
                                .padding(end = 5.dp),
                            style = TextStyle(fontSize = 12.sp, color = Color(0xFF2B7592)),
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
            item {
                Divider(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(top = 50.dp)
                )
            }
            item {
                //作者头像、名字、关注按钮
                Row(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(top = 10.dp)
                ) {
                    UserAvatar(
                        url = illust.user.profileImageUrls.medium,
                        size = 30.dp,
                        modifier = Modifier
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
                    //todo 点击关注按钮
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .border(
                                width = 1.dp,
                                color = Color(0xFF2B7592),
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        text = "关注",
                        style = TextStyle(
                            color = Color(0xFF2B7592),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(top = 10.dp)
                ) {
                    state.userIllusts.subList(0, minOf(USER_ILLUSTS_COUNT, state.userIllusts.size))
                        .forEach {
                            SquareIllustItem(
                                isBookmark = it.isBookmarked,
                                spanCount = minOf(USER_ILLUSTS_COUNT, state.userIllusts.size),
                                url = it.imageUrls.squareMedium,
                                imageCount = it.pageCount,
                                horizontalPadding = 15.dp,
                                paddingValues = PaddingValues(2.dp),
                                elevation = 1.dp,
                                onBookmarkClick = {
                                    bookmarkIllust(viewModel, it.id, it.isBookmarked)
                                }
                            ) {
                                navigateToPictureScreen(navHostController, it)
                            }
                        }
                }
            }
            item {
                //相关作品文字，显示在中间
                Text(
                    text = "相关作品",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
            item(key = "${illust.id}_related") {
                // 相关作品
                // 由于不能在LazyColumn中嵌套LazyColumn，所以这里用FlowRow代替
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 50.dp,
                            bottom = if (isScrollToRelatedBottom.value) 0.dp else 100.dp
                        ),
                    maxItemsInEachRow = 2
                ) {
                    state.illustRelated.forEach {
                        SquareIllustItem(
                            isBookmark = it.isBookmarked,
                            spanCount = 2,
                            url = it.imageUrls.squareMedium,
                            imageCount = it.pageCount,
                            paddingValues = PaddingValues(5.dp),
                            elevation = 5.dp,
                            onBookmarkClick = {
                                bookmarkIllust(viewModel, it.id, it.isBookmarked)
                            }
                        ) {
                            navigateToPictureScreen(navHostController, it)
                        }
                    }
                }
            }
            item(key = "loading") {
                if (isScrollToRelatedBottom.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        lazyListState.OnScrollToBottom(isScrollToBottom, illust.pageCount, illust.id)
        lazyListState.OnScrollToRelatedBottom(
            isScrollToBottom = isScrollToRelatedBottom,
            id = illust.id,
        )
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val (appbar, picInfo) = createRefs()
            AnimatedVisibility(
                visible = isBarVisible,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier
                        .constrainAs(appbar) {
                            top.linkTo(parent.top)
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                        .padding(top = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .click {
                                navHostController.popBackStack()
                            },
                    )
                    // 分享按钮
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .click {
                                val shareIntent = createShareIntent(
                                    "${illust.title} | ${illust.user.name} #pixiv https://www.pixiv.net/artworks/${illust.id}"
                                )
                                shareLauncher.launch(shareIntent)
                            },
                    )
                    Text(
                        text = "${currPage.value + 1}/${illust.pageCount}",
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
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
                        .background(MaterialTheme.colors.background)
                        .padding(vertical = 10.dp)
                ) {
                    UserAvatar(
                        url = illust.user.profileImageUrls.medium,
                        size = 20.dp,
                        modifier = Modifier
                            .padding(start = 20.dp)
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
    }
}

private fun createShareIntent(text: String): Intent {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, text)
    return Intent.createChooser(shareIntent, "Share")
}

@Composable
private fun LazyListState.OnScrollToBottom(
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
                        && lastVisibleItem.let { it.offset + it.size } <= layoutInfo.let { it.viewportEndOffset - it.viewportStartOffset }
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

@Composable
private fun LazyListState.OnScrollToRelatedBottom(
    isScrollToBottom: MutableState<Boolean>,
    id: Long,
) {
    // 判断是否滚动到相关作品最底部
    val isToBottom by remember {
        derivedStateOf {
            val lastVisibleItem =
                layoutInfo.visibleItemsInfo.find { it.key == "${id}_related" }
//            layoutInfo.visibleItemsInfo.forEachIndexed { i, it ->
//                Log.d(
//                    "TAG",
//                    "OnScrollToBottom: ${it.index} ${
//                        it.let { "${it.offset} ${it.size} ${layoutInfo.viewportStartOffset} ${layoutInfo.viewportEndOffset}" }
//                    }"
//                )
//            }

            if (lastVisibleItem != null) {
                return@derivedStateOf lastVisibleItem.index == layoutInfo.totalItemsCount - 2
                        && lastVisibleItem.let { it.offset + it.size } <= layoutInfo.let { it.viewportEndOffset - it.viewportStartOffset }
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

private fun bookmarkIllust(
    viewModel: PictureViewModel,
    id: Long,
    isBookmarked: Boolean
) {
    if (isBookmarked) {
        viewModel.dispatch(PictureAction.UnBookmarkIllust(id))
    } else {
        viewModel.dispatch(PictureAction.BookmarkIllust(id))
    }
}

private fun navigateToPictureScreen(navHostController: NavHostController, illust: Illust) {
    navHostController.navigate(
        "${Destination.PictureScreen.route}/${
            Base64.UrlSafe.encode(
                Json
                    .encodeToString(illust)
                    .encodeToByteArray()
            )
        }"
    )
}
