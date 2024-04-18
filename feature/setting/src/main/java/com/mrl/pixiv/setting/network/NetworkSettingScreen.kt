package com.mrl.pixiv.setting.network

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.setting.R
import com.mrl.pixiv.setting.components.SettingItem
import com.mrl.pixiv.setting.network.components.PictureSourceWidget
import com.mrl.pixiv.setting.viewmodel.SettingAction
import com.mrl.pixiv.setting.viewmodel.SettingState
import com.mrl.pixiv.setting.viewmodel.SettingViewModel
import com.mrl.pixiv.util.ToastUtil
import org.koin.androidx.compose.koinViewModel

@Composable
fun NetworkSettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = koinViewModel(),
    navHostController: NavHostController = LocalNavigator.currentOrThrow
) {
    NetworkSettingScreen_(
        modifier = modifier,
        state = viewModel.state,
        dispatch = viewModel::dispatch
    ) { navHostController.popBackStack() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun NetworkSettingScreen_(
    modifier: Modifier = Modifier,
    state: SettingState = SettingState.INITIAL,
    dispatch: (SettingAction) -> Unit = {},
    popBack: () -> Unit = {},
) {
    Screen(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.network_setting))
                },
                navigationIcon = {
                    IconButton(onClick = popBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
        ) {
            SettingItem {
                Column {
                    Text(
                        text = stringResource(R.string.enable_bypass_sniffing),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.close_to_use_ip_directly),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Switch(
                    checked = state.enableBypassSniffing,
                    onCheckedChange = { dispatch(SettingAction.SwitchBypassSniffing) }
                )
            }
            PictureSourceWidget(
                currentSelected = state.pictureSourceHost,
                savePictureSourceHost = {
                    dispatch(SettingAction.SavePictureSourceHost(it))
                    ToastUtil.safeShortToast(R.string.restart_app_to_take_effect)
                }
            )
        }
    }
}
