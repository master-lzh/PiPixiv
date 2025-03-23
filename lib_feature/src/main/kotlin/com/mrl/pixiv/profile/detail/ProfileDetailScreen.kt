package com.mrl.pixiv.profile.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.kts.spaceBy
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.components.UserAvatar
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.util.copyToClipboard
import com.mrl.pixiv.common.util.navigateToPictureScreen
import com.mrl.pixiv.common.util.throttleClick
import com.mrl.pixiv.common.viewmodel.asState
import com.mrl.pixiv.feature.R
import com.mrl.pixiv.profile.detail.components.IllustWidget
import com.mrl.pixiv.profile.detail.components.NovelBookmarkWidget
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SelfProfileDetailScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
    profileDetailViewModel: ProfileDetailViewModel = koinViewModel { parametersOf(Long.MIN_VALUE) },
) {
    ProfileDetailScreen(
        modifier = modifier,
        state = profileDetailViewModel.asState(),
        navToPictureScreen = navHostController::navigateToPictureScreen,
    ) { navHostController.popBackStack() }
}

@Composable
fun OtherProfileDetailScreen(
    uid: Long,
    modifier: Modifier = Modifier,
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
    profileDetailViewModel: ProfileDetailViewModel = koinViewModel { parametersOf(uid) },
) {
    ProfileDetailScreen(
        modifier = modifier,
        state = profileDetailViewModel.asState(),
        navToPictureScreen = navHostController::navigateToPictureScreen,
    ) { navHostController.popBackStack() }
}

private const val KEY_USER_AVATAR = "user_avatar"
private const val KEY_USER_INFO = "user_info"
private const val KEY_USER_ILLUSTS = "user_illusts"
private const val KEY_USER_BOOKMARKS_ILLUSTS = "user_bookmarks_illusts"
private const val KEY_USER_BOOKMARKS_NOVELS = "user_bookmarks_novels"
private const val KEY_SPACE = "space"

@Composable
internal fun ProfileDetailScreen(
    modifier: Modifier = Modifier,
    state: ProfileDetailState,
    navToPictureScreen: (Illust, String) -> Unit = { _, _ -> },
    popBack: () -> Unit = { },
) {
    val userInfo = state.userInfo
//    val backgroundHeight = when (LocalConfiguration.current.orientation) {
//        Configuration.ORIENTATION_PORTRAIT -> DisplayUtil.getScreenWidthDp() / 3
//        Configuration.ORIENTATION_LANDSCAPE -> DisplayUtil.getScreenHeightDp() / 3
//        else -> DisplayUtil.getScreenWidthDp() / 3
//    }
    val lazyListState = rememberLazyListState()

    val showTopBar by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex > 0 }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                verticalAlignment = CenterVertically
            ) {
                IconButton(
                    onClick = popBack,
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = null)
                }
                AnimatedVisibility(
                    visible = showTopBar,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        verticalAlignment = CenterVertically
                    ) {
                        UserAvatar(
                            url = userInfo.user.profileImageUrls.medium,
                            modifier = Modifier.size(50.dp),
                            contentScale = ContentScale.FillWidth,
                        )
                        Text(
                            modifier = Modifier.padding(start = 10.dp),
                            text = userInfo.user.name,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                }
            }
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(),
            state = lazyListState,
            contentPadding = PaddingValues(horizontal = 15.dp)
        ) {
            item(key = KEY_USER_AVATAR) {
                UserAvatar(
                    url = userInfo.user.profileImageUrls.medium,
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.FillWidth,
                )
            }
            item(key = KEY_USER_INFO) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = CenterVertically
                    ) {
                        Text(
                            text = userInfo.user.name,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                        if (userInfo.profile.isPremium) {
                            Image(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_profile_premium),
                                modifier = Modifier
                                    .padding(start = 5.dp)
                                    .size(20.dp),
                                contentDescription = null
                            )
                        }
                    }
                    //id点击可复制
                    Row(
                        horizontalArrangement = 5f.spaceBy,
                        verticalAlignment = CenterVertically,
                    ) {
                        Text(
                            text = "ID: ${userInfo.user.id}",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .throttleClick(indication = ripple(radius = 15.dp)) {
                                    copyToClipboard(userInfo.user.id.toString())
                                }
                                .padding(5.dp)
                        )
                    }
                    // 个人简介
                    if (userInfo.user.comment.isNotEmpty()) {
                        Text(
                            text = userInfo.user.comment,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                }
            }
            if (state.userIllusts.isNotEmpty()) {
                item(key = KEY_USER_ILLUSTS) {
                    // 插画、漫画网格组件
                    IllustWidget(
                        title = stringResource(RString.illustration_works),
                        endText = stringResource(
                            RString.illustration_count,
                            userInfo.profile.totalIllusts
                        ),
                        navToPictureScreen = navToPictureScreen,
                        illusts = state.userIllusts,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
            if (state.userBookmarksIllusts.isNotEmpty()) {
                item(key = KEY_USER_BOOKMARKS_ILLUSTS) {
                    // 插画、漫画收藏网格组件
                    IllustWidget(
                        title = stringResource(RString.illust_and_manga_liked),
                        endText = stringResource(RString.view_all),
                        navToPictureScreen = navToPictureScreen,
                        illusts = state.userBookmarksIllusts,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            item(key = KEY_USER_BOOKMARKS_NOVELS) {
                if (state.userBookmarksNovels.isNotEmpty()) {
                    // 小说收藏网格组件
                    NovelBookmarkWidget(
                        novels = state.userBookmarksNovels,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    )
                }
            }
            item(key = KEY_SPACE) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
        }
    }
}