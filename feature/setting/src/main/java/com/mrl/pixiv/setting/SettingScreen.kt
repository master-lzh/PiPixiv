package com.mrl.pixiv.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.setting.viewmodel.SettingState
import com.mrl.pixiv.setting.viewmodel.SettingViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = koinViewModel(),
    navHostController: NavHostController
) {
    SettingScreen_(
        state = viewModel.state
    )
}

@Preview
@Composable
internal fun SettingScreen_(
    state: SettingState = SettingState.INITIAL
) {
    Screen {

    }
}