package com.mrl.pixiv.profile

import android.os.Build
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.data.setting.SettingTheme
import com.mrl.pixiv.common.data.setting.getAppCompatDelegateThemeMode
import com.mrl.pixiv.common.datasource.local.mmkv.requireUserInfoFlow
import com.mrl.pixiv.common.ui.LocalAnimatedContentScope
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.LocalSharedTransitionScope
import com.mrl.pixiv.common.ui.components.UserAvatar
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.ui.item.SettingItem
import com.mrl.pixiv.common.util.*
import com.mrl.pixiv.common.viewmodel.asState
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

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
) {
    ProfileScreen_(
        modifier = modifier,
        state = viewModel.asState(),
        dispatch = viewModel::dispatch,
        navToProfileDetail = navHostController::navigateToProfileDetailScreen,
        navToSetting = navHostController::navigateToSettingScreen,
        navToHistory = navHostController::navigateToHistoryScreen,
        navToCollection = navHostController::navigateToSelfCollectionScreen,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun ProfileScreen_(
    modifier: Modifier = Modifier,
    state: ProfileState = ProfileState,
    dispatch: (ProfileAction) -> Unit = {},
    navToProfileDetail: (Long) -> Unit = {},
    navToSetting: () -> Unit = {},
    navToHistory: () -> Unit = {},
    navToCollection: () -> Unit = {},
) {
    val userInfo by requireUserInfoFlow.collectAsStateWithLifecycle()
    LifecycleResumeEffect(Unit) {
        dispatch(ProfileAction.GetUserInfo)
        onPauseOrDispose {}
    }
    Scaffold(
        topBar = {
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
                                    dispatch(ProfileAction.ChangeAppTheme(theme = theme))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // 头像和昵称
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    with(LocalSharedTransitionScope.currentOrThrow) {
                        UserAvatar(
                            url = userInfo.user.profileImageUrls.medium,
                            modifier = Modifier.size(80.dp),
                            onClick = {
                                navToProfileDetail(userInfo.user.id)
                            }
                        )
                        Column {
                            // 昵称
                            Text(
                                text = userInfo.user.name,
                                modifier = Modifier
                                    .sharedElement(
                                        rememberSharedContentState(key = "user-name-${userInfo.user.id}"),
                                        LocalAnimatedContentScope.currentOrThrow,
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
                                        LocalAnimatedContentScope.currentOrThrow,
                                        placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                                    )
                                    .skipToLookaheadSize()
                            )
                        }
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
                                indication = ripple()
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
                                text = stringResource(RString.preference),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    // 历史记录
                    SettingItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.History,
                                contentDescription = null
                            )
                        },
                        onClick = navToHistory
                    ) {
                        Text(
                            text = stringResource(RString.history),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    // 收藏
                    SettingItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Bookmarks,
                                contentDescription = null
                            )
                        },
                        onClick = navToCollection
                    ) {
                        Text(
                            text = stringResource(RString.collection),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}