package com.mrl.pixiv.splash.viewmodel

import android.content.Intent
import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.data.auth.GrantType
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.annotation.KoinViewModel
import kotlin.reflect.KClass

@Stable
data class SplashState(
    val isLoading: Boolean,
    val startDestination: KClass<*>?,
) : State {
    companion object {
        val INITIAL = SplashState(
            isLoading = true,
            startDestination = null
        )
    }
}

sealed class SplashAction : Action {
    data object RouteToHomeScreenIntent : SplashAction()
    data object RouteToLoginScreenIntent : SplashAction()
    data object IsLoginIntent : SplashAction()
    data object IsNeedRefreshTokenIntent : SplashAction()
    data class RefreshAccessTokenAndRouteIntent(val grantType: GrantType) : SplashAction()
    data object RefreshAccessTokenIntent : SplashAction()
}

@KoinViewModel
class SplashViewModel(
    reducer: SplashReducer,
    middleware: SplashMiddleware,
) : BaseViewModel<SplashState, SplashAction>(
    reducer = reducer,
    initialState = SplashState.INITIAL,
    middlewares = listOf(middleware)
) {
    val intent: MutableStateFlow<Intent?> = MutableStateFlow(null)
    override fun onStart() {
        dispatch(SplashAction.IsLoginIntent)
    }
}