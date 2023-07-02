package com.mrl.pixiv.profile

import android.util.Log
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.mrl.pixiv.common.coil.BlurTransformation
import com.mrl.pixiv.common.ui.components.UserAvatar
import com.mrl.pixiv.util.DisplayUtil
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import org.koin.androidx.compose.koinViewModel

@Composable
@Preview
fun ProfileScreen(
    navHostController: NavHostController = rememberNavController(),
    profileViewModel: ProfileViewModel = koinViewModel(),
) {
    val state by profileViewModel.uiStateFlow.collectAsStateWithLifecycle()
    val userInfo = state.userInfo
//    val userInfo = UserInfo()
    val backgroundHeight = DisplayUtil.getScreenWidthDp() / 3
    val collapsingToolbarScaffoldState = rememberCollapsingToolbarScaffoldState()
    CollapsingToolbarScaffold(
        modifier = Modifier,
        state = collapsingToolbarScaffoldState,
        toolbar = {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DisplayUtil.getStatusBarHeightDp(LocalContext.current as ComponentActivity) + 50.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(backgroundHeight)
            ) {
                val backgroundUrl = if (userInfo.backgroundImageURL?.isNotEmpty() == true) {
                    userInfo.backgroundImageURL
                } else {
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
                    .height(DisplayUtil.getStatusBarHeightDp(LocalContext.current as ComponentActivity) + 50.dp)
                    .graphicsLayer {
                        alpha = 1 - collapsingToolbarScaffoldState.toolbarState.progress
                    }
            ) {
                Row(
                    modifier = Modifier
                        .align(BottomStart)
                        .padding(start = 20.dp, end = 10.dp)
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

            var profilePhotoSize by remember { mutableStateOf(100.dp) }
            Box(
                modifier = Modifier
                    .padding(start = 15.dp, top = backgroundHeight - 50.dp)
                    .size(profilePhotoSize)
                    .road(Alignment.BottomStart, Alignment.TopStart)
                    .graphicsLayer {
                        alpha *= collapsingToolbarScaffoldState.toolbarState.progress
//                        alpha.takeIf { it >= 0.5f }?.let {
//                            profilePhotoSize *= it
//                        }
//                        size = Size(100f,100f)
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
            modifier = Modifier.fillMaxWidth()
        ) {


            item {
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
            }
            items(100) {
                Text(text = "test")
            }
        }
    }
}