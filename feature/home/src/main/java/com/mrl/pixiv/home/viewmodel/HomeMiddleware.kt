package com.mrl.pixiv.home.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.illust.IllustRecommendedResp
import com.mrl.pixiv.domain.auth.RefreshUserAccessTokenUseCase
import com.mrl.pixiv.home.R
import com.mrl.pixiv.repository.remote.IllustRemoteRepository
import com.mrl.pixiv.util.ToastUtil
import kotlinx.collections.immutable.toImmutableList

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
            is HomeAction.CollectExceptionFlow-> collectExceptionFlow()

            else -> {}
        }
    }

    private fun collectExceptionFlow() {
        launchUI {

        }
    }

    private fun refreshToken(state: HomeState) {
        launchNetwork {
            refreshUserAccessTokenUseCase {
                ToastUtil.safeShortToast(R.string.refresh_token_success)
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
                            recommendImageList = imageList.toImmutableList(),
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
                            recommendImageList = (state.recommendImageList + imageList).toImmutableList(),
                            isRefresh = false,
                            nextUrl = it.nextURL
                        )
                    )
                )
            }
        }
    }
}