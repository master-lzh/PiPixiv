package com.mrl.pixiv.login.oauth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mrl.pixiv.common.kts.spaceBy
import com.mrl.pixiv.common.compose.LocalNavigator
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.util.loginToMainScreen
import com.mrl.pixiv.common.viewmodel.asState
import org.koin.androidx.compose.koinViewModel

@Composable
fun OAuthLoginScreen(
    modifier: Modifier = Modifier,
    viewModel: OAuthLoginViewModel = koinViewModel()
) {
    val navigator = LocalNavigator.current
    var token by remember { mutableStateOf("") }
    val state = viewModel.asState()
    val focusManager = LocalFocusManager.current
    LaunchedEffect(state.isLogin) {
        if (state.isLogin) {
            navigator.loginToMainScreen()
        }
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = 10f.spaceBy
            ) {
                TextField(
                    value = token,
                    onValueChange = { token = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(RString.token))
                    }
                )
                Button(
                    onClick = click@{
                        if (state.loading || token.isEmpty()) return@click
                        focusManager.clearFocus()
                        viewModel.dispatch(OAuthLoginAction.Login(token))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(RString.sign_in)
                    )
                }
            }
            if (state.loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}