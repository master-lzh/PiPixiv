package com.mrl.pixiv.profile

import android.os.Build
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.compose.LocalAnimatedContentScope
import com.mrl.pixiv.common.compose.LocalNavigator
import com.mrl.pixiv.common.compose.LocalSharedTransitionScope
import com.mrl.pixiv.common.compose.ui.SettingItem
import com.mrl.pixiv.common.compose.ui.image.UserAvatar
import com.mrl.pixiv.common.data.setting.SettingTheme
import com.mrl.pixiv.common.data.setting.getAppCompatDelegateThemeMode
import com.mrl.pixiv.common.datasource.local.mmkv.requireUserInfoFlow
import com.mrl.pixiv.common.datasource.local.mmkv.requireUserInfoValue
import com.mrl.pixiv.common.util.*
import org.koin.androidx.compose.koinViewModel

private val options = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    mapOf(
        SettingTheme.SYSTEM to RString.theme_system,
        SettingTheme.LIGHT to RString.theme_light,
        SettingTheme.DARK to RString.theme_dark,
    )
} else {
    mapOf(
        SettingTheme.LIGHT to RString.theme_light,
        SettingTheme.DARK to RString.theme_dark,
    )
}

private const val KEY_USER_INFO = "user_info"
private const val KEY_DIVIDER = "divider"
private const val KEY_SETTINGS = "settings"

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
    navHostController: NavHostController = LocalNavigator.current,
) {
    val userInfo by requireUserInfoFlow.collectAsStateWithLifecycle()
    LifecycleResumeEffect(Unit) {
        viewModel.dispatch(ProfileAction.GetUserInfo)
        onPauseOrDispose {}
    }
    Scaffold(
        topBar = {
            ProfileAppBar(
                onChangeAppTheme = { theme ->
                    viewModel.dispatch(ProfileAction.ChangeAppTheme(theme = theme))
                }
            )
        }
    ) {
        LazyColumn(
            modifier = modifier
                .padding(it)
                .fillMaxSize()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = KEY_USER_INFO) {
                // 头像和昵称
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    with(LocalSharedTransitionScope.current) {
                        UserAvatar(
                            url = userInfo.user.profileImageUrls.medium,
                            modifier = Modifier.size(80.dp),
                            onClick = {
                                navHostController.navigateToProfileDetailScreen(userInfo.user.id)
                            }
                        )
                        Column {
                            // 昵称
                            Text(
                                text = userInfo.user.name,
                                modifier = Modifier
                                    .sharedElement(
                                        rememberSharedContentState(key = "user-name-${userInfo.user.id}"),
                                        LocalAnimatedContentScope.current,
                                        placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                                    )
                                    .skipToLookaheadSize()
                            )
                            // ID
                            Text(
                                text = "ID: ${userInfo.user.id}",
                                modifier = Modifier
                                    .sharedElement(
                                        rememberSharedContentState(key = "user-id-${userInfo.user.id}"),
                                        LocalAnimatedContentScope.current,
                                        placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                                    )
                                    .skipToLookaheadSize()
                            )
                        }
                    }
                }
            }
            item(key = KEY_DIVIDER) {
                HorizontalDivider()
            }
            item(key = KEY_SETTINGS) {
                Column {
                    // 偏好设置
                    SettingItem(
                        onClick = navHostController::navigateToSettingScreen,
                        icon = {
                            Icon(imageVector = Icons.Rounded.Settings, contentDescription = null)
                        },
                    ) {
                        Text(
                            text = stringResource(RString.preference),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    // 历史记录
                    SettingItem(
                        onClick = navHostController::navigateToHistoryScreen,
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.History,
                                contentDescription = null
                            )
                        },
                    ) {
                        Text(
                            text = stringResource(RString.history),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    // 收藏
                    SettingItem(
                        onClick = {
                            navHostController.navigateToCollectionScreen(requireUserInfoValue.user.id)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Bookmarks,
                                contentDescription = null
                            )
                        },
                    ) {
                        Text(
                            text = stringResource(RString.collection),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    // 导出Token
                    SettingItem(
                        onClick = {
                            viewModel.dispatch(ProfileAction.ExportToken)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.ImportExport,
                                contentDescription = null
                            )
                        },
                    ) {
                        Text(
                            text = stringResource(RString.export_token),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    // 退出登录
                    SettingItem(
                        onClick = {
                            viewModel.logout()
                            navHostController.navigateToLoginOptionScreen()
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Logout,
                                contentDescription = null
                            )
                        },
                    ) {
                        Text(
                            text = stringResource(RString.sign_out),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileAppBar(
    onChangeAppTheme: (SettingTheme) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    TopAppBar(
        title = {},
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(imageVector = Icons.Rounded.Palette, contentDescription = null)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (theme, resId) ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = stringResource(resId),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (getAppCompatDelegateThemeMode() == theme) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        onClick = {
                            onChangeAppTheme(theme)
                            expanded = false
                        }
                    )
                }
            }
        }
    )
}