package com.mrl.pixiv.profile.detail

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.lifecycle.OnLifecycle
import com.mrl.pixiv.common.ui.LocalAnimatedContentScope
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.LocalSharedTransitionScope
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.common.ui.components.UserAvatar
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.util.navigateToPictureScreen
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.profile.R
import com.mrl.pixiv.profile.detail.components.IllustWidget
import com.mrl.pixiv.profile.detail.components.NovelBookmarkWidget
import com.mrl.pixiv.profile.detail.viewmodel.ProfileDetailAction
import com.mrl.pixiv.profile.detail.viewmodel.ProfileDetailState
import com.mrl.pixiv.profile.detail.viewmodel.ProfileDetailViewModel
import com.mrl.pixiv.util.copyToClipboard
import com.mrl.pixiv.util.throttleClick
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SelfProfileDetailScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
    profileDetailViewModel: ProfileDetailViewModel = koinViewModel { parametersOf(Long.MIN_VALUE) },
) {
    OnLifecycle(onLifecycle = profileDetailViewModel::onStart)
    ProfileDetailScreen(
        modifier = modifier,
        state = profileDetailViewModel.state,
        navToPictureScreen = navHostController::navigateToPictureScreen,
        dispatch = profileDetailViewModel::dispatch,
        popBack = { navHostController.popBackStack() },
    )
}

@Composable
fun OtherProfileDetailScreen(
    uid: Long,
    modifier: Modifier = Modifier,
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
    profileDetailViewModel: ProfileDetailViewModel = koinViewModel { parametersOf(uid) },
) {
    OnLifecycle(onLifecycle = profileDetailViewModel::onStart)
    ProfileDetailScreen(
        modifier = modifier,
        state = profileDetailViewModel.state,
        navToPictureScreen = navHostController::navigateToPictureScreen,
        dispatch = profileDetailViewModel::dispatch,
        popBack = { navHostController.popBackStack() },
    )
}

@Composable
internal fun ProfileDetailScreen(
    modifier: Modifier = Modifier,
    state: ProfileDetailState,
    navToPictureScreen: (Illust, String) -> Unit = { _, _ -> },
    dispatch: (ProfileDetailAction) -> Unit,
    popBack: () -> Unit = { },
) {
    val userInfo = state.userInfo
//    val backgroundHeight = when (LocalConfiguration.current.orientation) {
//        Configuration.ORIENTATION_PORTRAIT -> DisplayUtil.getScreenWidthDp() / 3
//        Configuration.ORIENTATION_LANDSCAPE -> DisplayUtil.getScreenHeightDp() / 3
//        else -> DisplayUtil.getScreenWidthDp() / 3
//    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val avatarSize = 50.dp
    val collapseHeight = with(LocalDensity.current) {
        avatarSize + WindowInsets.statusBars.getTop(this).toDp() + 20.dp
    }

    Screen(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                    ) {
                        Row(
                            modifier = Modifier.align(CenterStart),
                        ) {
                            userInfo.user?.profileImageUrls?.medium?.let {
                                with(LocalSharedTransitionScope.currentOrThrow) {
                                    UserAvatar(
                                        url = it,
                                        modifier = Modifier.size(avatarSize * (2 - scrollBehavior.state.collapsedFraction))
                                            .sharedElement(
                                                state = rememberSharedContentState(key = "user-avatar-${userInfo.user.id}"),
                                                LocalAnimatedContentScope.currentOrThrow,
                                                placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                                            ),
                                        contentScale = ContentScale.FillWidth,
                                    )
                                }
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
                },
                navigationIcon = {
                    IconButton(
                        onClick = popBack,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(vertical = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = null)
                    }
                },
                collapsedHeight = collapseHeight,
                expandedHeight = 300.dp,
                windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal),
                scrollBehavior = scrollBehavior,
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(),
        ) {
            item(key = "user_info") {
                with(LocalSharedTransitionScope.currentOrThrow) {
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
                                modifier = Modifier
                                    .sharedElement(
                                        rememberSharedContentState(key = "user-name-${userInfo.user.id}"),
                                        LocalAnimatedContentScope.currentOrThrow,
                                        placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                                    )
                                    .skipToLookaheadSize()
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
                            modifier = Modifier
                                .sharedElement(
                                    rememberSharedContentState(key = "user-id-${userInfo.user?.id}"),
                                    LocalAnimatedContentScope.currentOrThrow,
                                    placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                                )
                                .skipToLookaheadSize(),
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