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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.ui.components.UserAvatar
import com.mrl.pixiv.common_viewmodel.bookmark.BookmarkViewModel
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.util.DisplayUtil
import com.mrl.pixiv.util.click
import com.mrl.pixiv.util.convertUtcStringToLocalDateTime
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val USER_ILLUSTS_COUNT = 3

@OptIn(ExperimentalLayoutApi::class, ExperimentalEncodingApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PictureScreen(
    illust: Illust,
    navHostController: NavHostController = rememberNavController(),
    viewModel: PictureViewModel = koinViewModel(),
    bookmarkViewModel: BookmarkViewModel = koinViewModel(),
) {
    val state by viewModel.uiStateFlow.collectAsStateWithLifecycle()
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
    var isBookmarked by remember { mutableStateOf(illust.isBookmarked) }
    val bookmarkStatus by bookmarkViewModel.bookmarkStatus.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.dispatch(PictureUiIntent.GetUserIllustsIntent(illust.user.id))
    }
    LaunchedEffect(bookmarkStatus) {
        bookmarkStatus?.let {
            isBookmarked = it
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
                            bookmarkViewModel.deleteBookmarkIllust(illust.id)
                        } else {
                            bookmarkViewModel.bookmarkIllust(illust.id)
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
                            contentScale = ContentScale.FillWidth
                        )
                    }
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(illust.metaSinglePage.originalImageURL)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
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
                Divider(modifier = Modifier.padding(horizontal = 15.dp))
            }
            item {
                Row {
                    state.userIllusts.subList(0, minOf(USER_ILLUSTS_COUNT, state.userIllusts.size))
                        .forEach {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(it.imageUrls.squareMedium)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(DisplayUtil.getScreenWidthDp() / USER_ILLUSTS_COUNT)
                                    .padding(5.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .click {
                                        navHostController.navigate(
                                            "${Destination.PictureScreen.route}/${
                                                Base64.UrlSafe.encode(
                                                    Json
                                                        .encodeToString(it)
                                                        .encodeToByteArray()
                                                )
                                            }"
                                        )
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                }
            }
            item {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
        lazyListState.OnScrollToBottom(isScrollToBottom, illust.pageCount, illust.id)
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
            layoutInfo.visibleItemsInfo.forEachIndexed { i, it ->
                Log.d(
                    "TAG",
                    "OnScrollToBottom: ${it.index} ${
                        it.let { "${it.offset} ${it.size} ${layoutInfo.viewportStartOffset} ${layoutInfo.viewportEndOffset}" }
                    }"
                )
            }


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