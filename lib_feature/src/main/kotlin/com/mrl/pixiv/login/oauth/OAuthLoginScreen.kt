package com.mrl.pixiv.login.oauth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mrl.pixiv.common.kts.spaceBy
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.util.loginToMainScreen
import com.mrl.pixiv.common.viewmodel.asState
import org.koin.androidx.compose.koinViewModel

@Composable
fun OAuthLoginScreen(
    modifier: Modifier = Modifier,
    viewModel: OAuthLoginViewModel = koinViewModel()
) {
    val navigator = LocalNavigator.currentOrThrow
    var token by remember { mutableStateOf("") }
    val state = viewModel.asState()
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
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp),
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
                onClick = {
                    viewModel.dispatch(OAuthLoginAction.Login(token))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(RString.sign_in)
                )
            }
        }
    }
}