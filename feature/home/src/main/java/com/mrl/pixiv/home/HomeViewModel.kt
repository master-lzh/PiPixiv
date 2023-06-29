package com.mrl.pixiv.home

import androidx.compose.runtime.toMutableStateList
import com.mrl.pixiv.common.base.BaseViewModel
import com.mrl.pixiv.common.data.failed
import com.mrl.pixiv.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.data.auth.AuthTokenResp
import com.mrl.pixiv.data.illust.IllustRecommendedResp
import com.mrl.pixiv.domain.SetUserAccessTokenUseCase
import com.mrl.pixiv.domain.SetUserIdUseCase
import com.mrl.pixiv.domain.SetUserRefreshTokenUseCase
import com.mrl.pixiv.home.components.recommendItemWidth
import com.mrl.pixiv.home.state.RecommendImageItemState
import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.repository.remote.AuthRemoteRepository
import com.mrl.pixiv.repository.remote.IllustRemoteRepository
import com.mrl.pixiv.util.ToastUtil
import kotlinx.coroutines.flow.first

class HomeViewModel(
    private val setUserAccessTokenUseCase: SetUserAccessTokenUseCase,
    private val setUserRefreshTokenUseCase: SetUserRefreshTokenUseCase,
    private val setUserIdUseCase: SetUserIdUseCase,
    private val authRemoteRepository: AuthRemoteRepository,
    private val userLocalRepository: UserLocalRepository,
    private val illustRemoteRepository: IllustRemoteRepository,
) : BaseViewModel<HomeUiState, HomeUiIntent>() {
    var nextUrl: String? = null

    init {
        dispatch(HomeUiIntent.RefreshIllustRecommendedIntent(initRecommendedQuery))
    }

    override fun handleUserIntent(intent: HomeUiIntent) {
        when (intent) {
            is HomeUiIntent.LoadMoreIllustRecommendedIntent -> loadMoreIllustRecommended(intent)
            is HomeUiIntent.IllustBookmarkAddIntent -> postIllustBookmarkAdd(intent)
            is HomeUiIntent.IllustBookmarkDeleteIntent -> postIllustBookmarkDelete(intent)
            is HomeUiIntent.RefreshIllustRecommendedIntent -> refreshIllustRecommended(intent)
            is HomeUiIntent.RefreshTokenIntent -> refreshToken(intent)
        }
    }

    private fun refreshToken(intent: HomeUiIntent.RefreshTokenIntent) {
        launchIO {
            requestHttpDataWithFlow(
                request = authRemoteRepository.login(
                    AuthTokenFieldReq(
                        grantType = intent.grantType.value,
                        refreshToken = userLocalRepository.userRefreshToken.first()
                    )
                )
            ) {
                if (it != null) {
                    setUserInfo(it)
                    updateUiState { apply { refreshTokenResult = true } }
                }
            }
        }
    }

    private fun loadMoreIllustRecommended(intent: HomeUiIntent.LoadMoreIllustRecommendedIntent) {
        launchIO {
            if (intent.queryMap == null) {
                updateUiState { failed(Throwable("no next page")) }
                return@launchIO
            }
            updateUiState { apply { loadMore = true } }
            requestHttpDataWithFlow(
                request = illustRemoteRepository.loadMoreIllustRecommended(intent.queryMap)
            ) {
                val imageList = handleRecommendResp(it)
                updateUiState {
                    apply {
                        recommendImageList += imageList
                        isRefresh = false
                        if (it?.nextURL != null) {
                            nextUrl = it.nextURL!!
                        }
                    }
                }
            }
        }
    }

    private fun refreshIllustRecommended(intent: HomeUiIntent.RefreshIllustRecommendedIntent) {
        launchIO {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.getIllustRecommended(intent.illustRecommendedQuery)
            ) {
                val imageList = handleRecommendResp(it)
                updateUiState {
                    apply {
                        recommendImageList = imageList.toMutableStateList()
                        isRefresh = true
                        if (it?.nextURL != null) {
                            nextUrl = it.nextURL!!
                        }
                    }
                }
            }
        }
    }

    private fun handleRecommendResp(resp: IllustRecommendedResp?): List<RecommendImageItemState> {
        return resp?.let { it.illusts + it.rankingIllusts }
            ?.map {
                val thumbnail =
                    it.imageUrls.original ?: it.imageUrls.large ?: it.imageUrls.medium
                    ?: it.imageUrls.squareMedium
                    ?: ""
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
                }
            } ?: emptyList()
    }

    private fun postIllustBookmarkDelete(intent: HomeUiIntent.IllustBookmarkDeleteIntent) {
        launchIO {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.postIllustBookmarkDelete(intent.illustBookmarkDeleteReq),
                failedCallback = {
                    ToastUtil.shortToast(R.string.bookmark_delete_failed)
                }
            ) {
                updateUiState {
                    apply {
                        val index =
                            recommendImageList.indexOfFirst { it.id == intent.illustBookmarkDeleteReq.illustId }
                        if (index != -1) {
                            recommendImageList.apply {
                                set(index, get(index).apply { isBookmarked = false })
                            }
                        }
                    }
                }
                ToastUtil.shortToast(R.string.bookmark_delete_success)
            }
        }
    }

    private fun postIllustBookmarkAdd(intent: HomeUiIntent.IllustBookmarkAddIntent) {
        launchIO {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.postIllustBookmarkAdd(intent.illustBookmarkAddReq),
                failedCallback = {
                    ToastUtil.shortToast(R.string.bookmark_add_failed)
                }
            ) {
                updateUiState {
                    apply {
                        val index =
                            recommendImageList.indexOfFirst { it.id == intent.illustBookmarkAddReq.illustId }
                        if (index != -1) {
                            recommendImageList.apply {
                                set(index, get(index).apply { isBookmarked = true })
                            }
                        }
                    }
                }
                ToastUtil.shortToast(R.string.bookmark_add_success)
            }
        }
    }

    private fun setUserInfo(authTokenResp: AuthTokenResp) = launchIO {
        authTokenResp.apply {
            user?.id?.let { setUserIdUseCase(it.toLong()) }
            accessToken?.let { setUserAccessTokenUseCase(it) }
            refreshToken?.let { setUserRefreshTokenUseCase(it) }
        }
    }

    override fun initUiState(): HomeUiState = HomeUiState()

}