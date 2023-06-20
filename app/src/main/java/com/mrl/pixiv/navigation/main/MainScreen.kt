package com.mrl.pixiv.navigation.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.ui.BaseScreen

@Composable
fun MainScreen(
    navHostController: NavHostController
) {
    BaseScreen {
        MainGraph(navHostController)
    }
}