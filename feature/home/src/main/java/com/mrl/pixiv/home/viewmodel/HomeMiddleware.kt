package com.mrl.pixiv.home.viewmodel

import com.mrl.pixiv.common.data.Middleware
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.illust.IllustRecommendedResp
import com.mrl.pixiv.domain.auth.RefreshUserAccessTokenUseCase
import com.mrl.pixiv.repository.remote.IllustRemoteRepository

class HomeMiddleware(
    private val refreshUserAccessTokenUseCase: RefreshUserAccessTokenUseCase,
    private val illustRemoteRepository: IllustRemoteRepository,
) : Middleware<HomeState, HomeAction>() {
    override suspend fun process(state: HomeState, action: HomeAction) {
        when (action) {
            is HomeAction.LoadMoreIllustRecommendedIntent -> loadMoreIllustRecommended(
                state,
                action
            )

            is HomeAction.RefreshIllustRecommendedIntent -> refreshIllustRecommended(state, action)
            is HomeAction.RefreshTokenIntent -> refreshToken(state)

            else -> {}
        }
    }

    private fun refreshToken(state: HomeState) {
        launchNetwork {
            refreshUserAccessTokenUseCase {
                dispatch(HomeAction.UpdateState(state.copy(refreshTokenResult = true)))
            }
        }
    }

    private fun refreshIllustRecommended(
        state: HomeState,
        action: HomeAction.RefreshIllustRecommendedIntent
    ) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.getIllustRecommended(action.illustRecommendedQuery)
            ) {
                val imageList = handleRecommendResp(it)
                dispatch(
                    HomeAction.UpdateState(
                        state.copy(
                            recommendImageList = imageList,
                            isRefresh = false,
                            nextUrl = it.nextURL
                        )
                    )
                )
            }
        }
    }

    private fun handleRecommendResp(resp: IllustRecommendedResp?): List<Illust> {
        return resp?.let { it.illusts + it.rankingIllusts } ?: emptyList()
    }

    private fun loadMoreIllustRecommended(
        state: HomeState,
        action: HomeAction.LoadMoreIllustRecommendedIntent
    ) {
        launchNetwork {
            if (action.queryMap == null) {
                return@launchNetwork
            }
            dispatch(HomeAction.UpdateState(state.copy(loadMore = true)))
            requestHttpDataWithFlow(
                request = illustRemoteRepository.loadMoreIllustRecommended(action.queryMap)
            ) {
                val imageList = handleRecommendResp(it)
                dispatch(
                    HomeAction.UpdateState(
                        state.copy(
                            recommendImageList = state.recommendImageList + imageList,
                            isRefresh = false,
                            nextUrl = it.nextURL
                        )
                    )
                )
            }
        }
    }
}