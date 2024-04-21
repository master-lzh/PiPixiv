package com.mrl.pixiv.profile.detail

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.mrl.pixiv.common.coil.BlurTransformation
import com.mrl.pixiv.common.lifecycle.OnLifecycle
import com.mrl.pixiv.common.middleware.bookmark.BookmarkAction
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.common.middleware.bookmark.BookmarkViewModel
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.components.UserAvatar
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common_ui.util.navigateToPictureScreen
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.profile.R
import com.mrl.pixiv.profile.detail.components.IllustWidget
import com.mrl.pixiv.profile.detail.components.NovelBookmarkWidget
import com.mrl.pixiv.profile.detail.viewmodel.ProfileDetailAction
import com.mrl.pixiv.profile.detail.viewmodel.ProfileDetailState
import com.mrl.pixiv.profile.detail.viewmodel.ProfileDetailViewModel
import com.mrl.pixiv.util.DisplayUtil
import com.mrl.pixiv.util.copyToClipboard
import com.mrl.pixiv.util.throttleClick
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SelfProfileDetailScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
    bookmarkViewModel: BookmarkViewModel,
    profileDetailViewModel: ProfileDetailViewModel = koinViewModel { parametersOf(Long.MIN_VALUE) },
) {
    OnLifecycle(onLifecycle = profileDetailViewModel::onStart)
    ProfileDetailScreen(
        modifier = modifier,
        state = profileDetailViewModel.state,
        bookmarkState = bookmarkViewModel.state,
        bookmarkDispatch = bookmarkViewModel::dispatch,
        navToPictureScreen = navHostController::navigateToPictureScreen,
        popBack = { navHostController.popBackStack() },
        dispatch = profileDetailViewModel::dispatch,
    )
}

@Composable
fun OtherProfileDetailScreen(
    uid: Long,
    modifier: Modifier = Modifier,
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
    bookmarkViewModel: BookmarkViewModel,
    profileDetailViewModel: ProfileDetailViewModel = koinViewModel { parametersOf(uid) },
) {
    OnLifecycle(onLifecycle = profileDetailViewModel::onStart)
    ProfileDetailScreen(
        modifier = modifier,
        state = profileDetailViewModel.state,
        bookmarkState = bookmarkViewModel.state,
        bookmarkDispatch = bookmarkViewModel::dispatch,
        navToPictureScreen = navHostController::navigateToPictureScreen,
        popBack = { navHostController.popBackStack() },
        dispatch = profileDetailViewModel::dispatch,
    )
}

@Composable
internal fun ProfileDetailScreen(
    modifier: Modifier = Modifier,
    state: ProfileDetailState,
    bookmarkState: BookmarkState,
    bookmarkDispatch: (BookmarkAction) -> Unit,
    navToPictureScreen: (Illust) -> Unit = {},
    dispatch: (ProfileDetailAction) -> Unit,
    popBack: () -> Unit = { },
) {
    val userInfo = state.userInfo
    val backgroundHeight = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> DisplayUtil.getScreenWidthDp() / 3
        Configuration.ORIENTATION_LANDSCAPE -> DisplayUtil.getScreenHeightDp() / 3
        else -> DisplayUtil.getScreenWidthDp() / 3
    }
    val collapsingToolbarScaffoldState = rememberCollapsingToolbarScaffoldState()

    CollapsingToolbarScaffold(
        modifier = modifier.fillMaxSize(),
        state = collapsingToolbarScaffoldState,
        toolbar = {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(backgroundHeight)
            ) {
                val backgroundUrl = userInfo.backgroundImageURL.ifEmpty {
                    userInfo.user?.profileImageUrls?.medium
                }
                if (!backgroundUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(backgroundUrl).allowRgb565(true)
                            .transformations(BlurTransformation(LocalContext.current))
                            .build(),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            IconButton(
                onClick = popBack,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(vertical = 10.dp)
            ) {
                Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = null)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .graphicsLayer {
                        alpha = 1 - collapsingToolbarScaffoldState.toolbarState.progress
                    }
            ) {
                Row(
                    modifier = Modifier
                        .align(CenterStart)
                        .padding(start = 50.dp)
                        .padding(vertical = 10.dp)
                        .graphicsLayer {
                            alpha = 1 - collapsingToolbarScaffoldState.toolbarState.progress
                        },
                ) {
                    userInfo.user?.profileImageUrls?.medium?.let {
                        UserAvatar(
                            url = it,
                            modifier = Modifier.size(50.dp),
                            contentScale = ContentScale.FillWidth,
                        )
                    }
                    userInfo.user?.name?.let { it1 ->
                        Text(
                            modifier = Modifier
                                .align(CenterVertically)
                                .padding(start = 10.dp),
                            text = it1,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(start = 15.dp, top = backgroundHeight - 50.dp)
                    .size(100.dp)
                    .road(BottomStart, Alignment.TopStart)
                    .graphicsLayer {
                        alpha *= collapsingToolbarScaffoldState.toolbarState.progress
                    }
            ) {
                userInfo.user?.profileImageUrls?.medium?.let {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it).allowRgb565(true)
                            .transformations(CircleCropTransformation())
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        enabled = true,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            item(key = "user_info") {
                Row(
                    modifier = Modifier
                        .padding(start = 15.dp, top = 10.dp)
                ) {
                    userInfo.user?.name?.let { it1 ->
                        Text(
                            text = it1,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                    if (userInfo.isPremium) {
                        Image(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_profile_premium),
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .size(20.dp)
                                .align(CenterVertically),
                            contentDescription = null
                        )
                    }
                }
                //id点击可复制
                Row(
                    modifier = Modifier
                        .padding(start = 15.dp, top = 10.dp)
                        .throttleClick {
                            userInfo.user?.id?.let { it1 -> copyToClipboard(it1.toString()) }
                        }
                ) {
                    Text(
                        text = "ID: ${userInfo.user?.id}",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
                // 个人简介
                userInfo.user?.comment?.let {
                    Row(
                        modifier = Modifier
                            .padding(start = 15.dp, top = 10.dp)
                    ) {
                        Text(
                            text = it,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
            item(key = "user_illusts") {
                if (state.userIllusts.isNotEmpty()) {
                    // 插画、漫画网格组件
                    IllustWidget(
                        title = stringResource(R.string.illustration_works),
                        endText = stringResource(
                            R.string.illustration_count,
                            state.userInfo.totalIllusts
                        ),
                        bookmarkState = bookmarkState,
                        bookmarkDispatch = bookmarkDispatch,
                        navToPictureScreen = navToPictureScreen,
                        illusts = state.userIllusts
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
            item(key = "user_bookmarks_illusts") {
                if (state.userBookmarksIllusts.isNotEmpty()) {
                    // 插画、漫画收藏网格组件
                    IllustWidget(
                        title = stringResource(R.string.illust_and_manga_liked),
                        endText = stringResource(R.string.view_all),
                        bookmarkState = bookmarkState,
                        bookmarkDispatch = bookmarkDispatch,
                        navToPictureScreen = navToPictureScreen,
                        illusts = state.userBookmarksIllusts
                    )
                }
            }
            item(key = "user_bookmarks_novels") {
                if (state.userBookmarksNovels.isNotEmpty()) {
                    // 小说收藏网格组件
                    NovelBookmarkWidget(
                        novels = state.userBookmarksNovels
                    )
                }
            }
            item(key = "space") {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
        }
    }
}