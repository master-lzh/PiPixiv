package com.mrl.pixiv.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
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
import com.mrl.pixiv.common.ui.BaseScreen
import com.mrl.pixiv.util.DisplayUtil
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
    BaseScreen {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .padding(start = 15.dp, top = backgroundHeight - 50.dp)
                        .size(100.dp)

                ) {
                    userInfo.user?.profileImageUrls?.medium?.let {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(it).allowRgb565(true)
                                .transformations(CircleCropTransformation())
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }

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
    }
}