package com.mrl.pixiv.home.viewmodel

import com.mrl.pixiv.common.data.Middleware
import com.mrl.pixiv.data.illust.IllustRecommendedResp
import com.mrl.pixiv.domain.auth.RefreshUserAccessTokenUseCase
import com.mrl.pixiv.domain.bookmark.BookmarkUseCase
import com.mrl.pixiv.domain.bookmark.UnBookmarkUseCase
import com.mrl.pixiv.home.components.recommendItemWidth
import com.mrl.pixiv.home.state.RecommendImageItemState
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
                                recommendImageList = imageList,
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
                val thumbnail =
                    it.imageUrls.original.ifEmpty { it.imageUrls.large.ifEmpty { it.imageUrls.medium.ifEmpty { it.imageUrls.squareMedium } } }
                RecommendImageItemState().apply {
                    // 宽高比
                    val scale = it.height * 1.0f / it.width
                    id = it.id
                    this.thumbnail = thumbnail
                    originImageUrl = it.metaSinglePage.originalImageURL
                    originImageUrls = it.metaPages?.mapNotNull { it3 ->
                        it3.imageUrls?.original
                    }
                    title = it.title
                    author = it.user.name
                    width = recommendItemWidth
                    height = (width * scale).toInt()
                    totalView = it.totalView
                    totalBookmarks = it.totalBookmarks
                    isBookmarked = it.isBookmarked
                    illust = it
                }
            } ?: emptyList()
    }


    private fun postIllustBookmarkDelete(
        state: HomeState,
        action: HomeAction.IllustBookmarkDeleteIntent
    ) {
        unBookmarkUseCase(action.illustBookmarkDeleteReq) {
            dispatch(HomeAction.UpdateState(state.copy(
                recommendImageList = state.recommendImageList.map {
                    if (it.id == action.illustBookmarkDeleteReq.illustId) {
                        it.isBookmarked = false
                    }
                    it
                }
            )))
        }
    }

    private fun postIllustBookmarkAdd(
        state: HomeState,
        action: HomeAction.IllustBookmarkAddIntent
    ) {
        bookmarkUseCase(action.illustBookmarkAddReq) {
            dispatch(HomeAction.UpdateState(state.copy(
                recommendImageList = state.recommendImageList.map {
                    if (it.id == action.illustBookmarkAddReq.illustId) {
                        it.isBookmarked = true
                    }
                    it
                }
            )))
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
}