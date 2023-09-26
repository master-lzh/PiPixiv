package com.mrl.pixiv.home.viewmodel

import androidx.compose.runtime.toMutableStateList
import com.mrl.pixiv.common.data.Middleware
import com.mrl.pixiv.data.illust.IllustRecommendedResp
import com.mrl.pixiv.domain.auth.RefreshUserAccessTokenUseCase
import com.mrl.pixiv.domain.bookmark.BookmarkUseCase
import com.mrl.pixiv.domain.bookmark.UnBookmarkUseCase
import com.mrl.pixiv.home.components.recommendItemWidth
import com.mrl.pixiv.home.state.RecommendImageItemState
import com.mrl.pixiv.home.state.toRecommendImageItemState
import com.mrl.pixiv.repository.remote.IllustRemoteRepository

class HomeMiddleware(
    private val refreshUserAccessTokenUseCase: RefreshUserAccessTokenUseCase,
    private val bookmarkUseCase: BookmarkUseCase,
    private val unBookmarkUseCase: UnBookmarkUseCase,
    private val illustRemoteRepository: IllustRemoteRepository,
) : Middleware<HomeState, HomeAction>() {
    override suspend fun process(state: HomeState, action: HomeAction) {
        when (action) {
            is HomeAction.LoadMoreIllustRecommendedIntent -> loadMoreIllustRecommended(
                state,
                action
            )

            is HomeAction.IllustBookmarkAddIntent -> postIllustBookmarkAdd(state, action)
            is HomeAction.IllustBookmarkDeleteIntent -> postIllustBookmarkDelete(state, action)
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
                if (it != null) {
                    dispatch(
                        HomeAction.UpdateState(
                            state.copy(
                                recommendImageList = imageList.toMutableStateList(),
                                isRefresh = false,
                                nextUrl = it.nextURL
                            )
                        )
                    )
                }
            }
        }
    }

    private fun handleRecommendResp(resp: IllustRecommendedResp?): List<RecommendImageItemState> {
        return resp?.let { it.illusts + it.rankingIllusts }
            ?.map {
                it.toRecommendImageItemState(recommendItemWidth)
            } ?: emptyList()
    }


    private fun postIllustBookmarkDelete(
        state: HomeState,
        action: HomeAction.IllustBookmarkDeleteIntent
    ) {
        unBookmarkUseCase(action.illustBookmarkDeleteReq) {
            state.recommendImageList.indexOfFirst { it.id == action.illustBookmarkDeleteReq.illustId }
                .takeIf {
                    it != -1
                }?.let { index ->
                    dispatch(HomeAction.UpdateState(state.copy(
                        recommendImageList = state.recommendImageList.apply {
                            set(index, get(index).copy(isBookmarked = false))
                        }
                    )))
                }
        }
    }

    private fun postIllustBookmarkAdd(
        state: HomeState,
        action: HomeAction.IllustBookmarkAddIntent
    ) {
        bookmarkUseCase(action.illustBookmarkAddReq) {
            state.recommendImageList.indexOfFirst { it.id == action.illustBookmarkAddReq.illustId }
                .takeIf {
                    it != -1
                }?.let { index ->
                    dispatch(HomeAction.UpdateState(state.copy(
                        recommendImageList = state.recommendImageList.apply {
                            set(index, get(index).copy(isBookmarked = true))
                        }
                    )))
                }
        }
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
                if (it != null) {
                    dispatch(
                        HomeAction.UpdateState(
                            state.copy(
                                recommendImageList = state.recommendImageList.apply {
                                    addAll(imageList)
                                },
                                isRefresh = false,
                                nextUrl = it.nextURL
                            )
                        )
                    )
                }
            }
        }
    }
}