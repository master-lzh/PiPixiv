package com.mrl.pixiv.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.common.ui.components.UserAvatar
import com.mrl.pixiv.common_ui.util.navigateToProfileDetailScreen
import com.mrl.pixiv.common_ui.util.navigateToSettingScreen
import com.mrl.pixiv.profile.viewmodel.ProfileState
import com.mrl.pixiv.profile.viewmodel.ProfileViewModel
import com.mrl.pixiv.util.throttleClick
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
    navHostController: NavHostController,
) {
    ProfileScreen_(
        modifier = modifier,
        state = viewModel.state,
        navToProfileDetail = navHostController::navigateToProfileDetailScreen,
        navToSetting = navHostController::navigateToSettingScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun ProfileScreen_(
    modifier: Modifier = Modifier,
    state: ProfileState = ProfileState.INITIAL,
    navToProfileDetail: () -> Unit = {},
    navToSetting: () -> Unit = {},
) {
    Screen(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Rounded.Palette, contentDescription = null)
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // 头像和昵称
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UserAvatar(
                        url = state.user.avatar,
                        modifier = Modifier.size(80.dp),
                        onClick = navToProfileDetail
                    )
                    Column {
                        // 昵称
                        Text(text = state.user.username)
                        // ID
                        Text(text = "ID: ${state.user.uid}")
                    }
                }
            }
            item {
                HorizontalDivider()
            }
            item {
                Column {
                    // 偏好设置
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .throttleClick(
                                indication = rememberRipple()
                            ) {
                                navToSetting()
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(imageVector = Icons.Rounded.Settings, contentDescription = null)
                            Text(
                                text = stringResource(R.string.preference),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}