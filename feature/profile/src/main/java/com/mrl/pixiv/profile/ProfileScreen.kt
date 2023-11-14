package com.mrl.pixiv.profile

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.mrl.pixiv.common.compose.OnLifecycle
import com.mrl.pixiv.common.ui.components.UserAvatar
import com.mrl.pixiv.common_ui.util.navigateToPictureScreen
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.profile.components.IllustBookmarkWidget
import com.mrl.pixiv.profile.components.NovelBookmarkWidget
import com.mrl.pixiv.profile.viewmodel.ProfileAction
import com.mrl.pixiv.profile.viewmodel.ProfileState
import com.mrl.pixiv.profile.viewmodel.ProfileViewModel
import com.mrl.pixiv.util.DisplayUtil
import com.mrl.pixiv.util.click
import com.mrl.pixiv.util.copyToClipboard
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    profileViewModel: ProfileViewModel = koinViewModel(),
) {
    OnLifecycle(onLifecycle = profileViewModel::onStart)
    ProfileScreen(
        modifier = modifier,
        state = profileViewModel.state,
        navToPictureScreen = navHostController::navigateToPictureScreen,
        dispatch = profileViewModel::dispatch,
    )
}

@Composable
internal fun ProfileScreen(
    modifier: Modifier = Modifier,
    state: ProfileState,
    navToPictureScreen: (Illust) -> Unit = {},
    dispatch: (ProfileAction) -> Unit,
) {
//    LaunchedEffect(Unit) {
//        profileViewModel.dispatch(ProfileAction.GetUserInfoIntent)
//        profileViewModel.dispatch(ProfileAction.GetUserBookmarksIllustIntent)
//    }
    val userInfo = state.userInfo
//    val userInfo = UserInfo()
    val backgroundHeight = DisplayUtil.getScreenWidthDp() / 3
    val collapsingToolbarScaffoldState = rememberCollapsingToolbarScaffoldState()

    CollapsingToolbarScaffold(
        modifier = Modifier,
        state = collapsingToolbarScaffoldState,
        toolbar = {
            val toolbarHeight = DisplayUtil.getStatusBarHeightDp(LocalContext.current as ComponentActivity) + 60.dp
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(toolbarHeight)
            )

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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(toolbarHeight)
                    .graphicsLayer {
                        alpha = 1 - collapsingToolbarScaffoldState.toolbarState.progress
                    }
            ) {
                Row(
                    modifier = Modifier
                        .align(BottomStart)
                        .padding(start = 20.dp, end = 10.dp)
                        .padding(bottom = 10.dp)
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
                    .road(Alignment.BottomStart, Alignment.TopStart)
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
                        .click {
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
                    Divider(modifier = Modifier.padding(horizontal = 15.dp))
                }
            }
            item(key = "user_bookmarks_illusts") {
                // 插画、漫画收藏网格组件
                IllustBookmarkWidget(
                    navToPictureScreen = navToPictureScreen,
                    illusts = state.userBookmarksIllusts
                )
            }
            item(key = "user_bookmarks_novels") {
                NovelBookmarkWidget(
                    novels = state.userBookmarksNovels
                )
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