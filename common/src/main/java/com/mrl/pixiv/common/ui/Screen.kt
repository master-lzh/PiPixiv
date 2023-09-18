package com.mrl.pixiv.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.primarySurface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mrl.pixiv.common.ui.components.LoadingDots

@Composable
fun BaseScreen(
    modifier: Modifier = Modifier,
    title: String = "",
    isLoading: Boolean = false,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    snackbarHost: @Composable (SnackbarHostState) -> Unit = {},
    actions: @Composable() (RowScope.() -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable (PaddingValues) -> Unit
) {
    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }
    rememberSystemUiController().setStatusBarColor(Color.Transparent)
    MaterialTheme(
        colors = colors,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
    ) {
        Scaffold(
            modifier = modifier,
            scaffoldState = scaffoldState,
            snackbarHost = snackbarHost,
            topBar = {
                actions?.let {
                    Column {
                        Spacer(
                            Modifier
                                .background(MaterialTheme.colors.primarySurface)
                                .windowInsetsTopHeight(WindowInsets.statusBars) // Match the height of the status bar
                                .fillMaxWidth()
                        )
                        TopAppBar(
                            title = { title?.let { Text(text = it) } },
                            actions = it,
                            elevation = 0.dp,
                        )
                    }
                }
            },
            bottomBar = bottomBar ?: {},
        ) {
            Box {
                content(it)
                LoadingDots(isLoading = isLoading)
            }
        }
    }
}