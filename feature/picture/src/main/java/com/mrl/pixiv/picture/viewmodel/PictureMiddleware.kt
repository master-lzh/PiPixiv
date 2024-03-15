package com.mrl.pixiv.picture.viewmodel

import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.request.ImageRequest
import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.data.Type
import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
import com.mrl.pixiv.data.illust.IllustRelatedQuery
import com.mrl.pixiv.data.user.UserIllustsQuery
import com.mrl.pixiv.repository.remote.IllustRemoteRepository
import com.mrl.pixiv.repository.remote.UserRemoteRepository
import com.mrl.pixiv.util.AppUtil
import com.mrl.pixiv.util.PictureType
import com.mrl.pixiv.util.saveToAlbum
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds


class PictureMiddleware(
    private val illustRemoteRepository: IllustRemoteRepository,
    private val userRemoteRepository: UserRemoteRepository,
) : Middleware<PictureState, PictureAction>() {
    override suspend fun process(state: PictureState, action: PictureAction) {
        when (action) {
            is PictureAction.GetUserIllustsIntent -> getUserIllusts(state, action.userId)
            is PictureAction.GetIllustRelatedIntent -> getIllustRelated(state, action.illustId)
            is PictureAction.LoadMoreIllustRelatedIntent -> loadMoreIllustRelated(
                state,
                action.queryMap
            )

            is PictureAction.BookmarkIllust -> bookmark(state, action.illustId)

            is PictureAction.UnBookmarkIllust -> unBookmark(state, action.illustId)
            is PictureAction.DownloadIllust -> downloadIllust(
                action.illustId,
                action.index,
                action.originalUrl,
                action.downloadCallback
            )

            else -> {}
        }
    }

    private fun downloadIllust(
        illustId: Long,
        index: Int,
        originalUrl: String,
        downloadCallback: (result: Boolean) -> Unit
    ) {
        launchNetwork {
            // 使用coil下载图片
            val imageLoader = Coil.imageLoader(AppUtil.appContext)
            val request = ImageRequest.Builder(AppUtil.appContext)
                .data(originalUrl)
                .build()
            val result = withTimeoutOrNull(10.seconds) {
                imageLoader.execute(request)
            }
            result ?: run {
                downloadCallback(false)
                return@launchNetwork
            }
            val file =
                result.drawable?.toBitmap()?.saveToAlbum("${illustId}_$index", PictureType.PNG)
            if (file != null) {
                downloadCallback(true)
            }
        }
    }

    private fun unBookmark(state: PictureState, illustId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.postIllustBookmarkDelete(
                    IllustBookmarkDeleteReq(
                        illustId
                    )
                )
            ) {
                dispatch(
                    PictureAction.UpdateIsBookmarkState(
                        userIllusts = state.userIllusts.apply {
                            indexOfFirst { it.id == illustId }.takeIf { it != -1 }?.let {
                                set(it, get(it).copy(isBookmarked = false))
                            }
                        },
                        illustRelated = state.illustRelated.apply {
                            indexOfFirst { it.id == illustId }.takeIf { it != -1 }?.let {
                                set(it, get(it).copy(isBookmarked = false))
                            }
                        }
                    )
                )
            }
        }
    }

    private fun bookmark(state: PictureState, illustId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.postIllustBookmarkAdd(IllustBookmarkAddReq(illustId))
            ) {
                dispatch(
                    PictureAction.UpdateIsBookmarkState(
                        userIllusts = state.userIllusts.apply {
                            indexOfFirst { it.id == illustId }.takeIf { it != -1 }?.let {
                                set(it, get(it).copy(isBookmarked = true))
                            }
                        },
                        illustRelated = state.illustRelated.apply {
                            indexOfFirst { it.id == illustId }.takeIf { it != -1 }?.let {
                                set(it, get(it).copy(isBookmarked = true))
                            }
                        }
                    )
                )
            }
        }
    }

    private fun loadMoreIllustRelated(state: PictureState, queryMap: Map<String, String>?) =
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.loadMoreIllustRelated(
                    queryMap ?: return@launchNetwork
                )
            ) {
                dispatch(
                    PictureAction.UpdateIllustRelatedState(
                        illustRelated = state.illustRelated + it.illusts,
                        nextUrl = it.nextURL
                    )
                )
            }
        }

    private fun getIllustRelated(state: PictureState, illustId: Long) =
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.getIllustRelated(
                    IllustRelatedQuery(
                        illustId = illustId,
                        filter = Filter.ANDROID.value
                    )
                )
            ) {
                dispatch(
                    PictureAction.UpdateIllustRelatedState(
                        illustRelated = it.illusts,
                        nextUrl = it.nextURL
                    )
                )
            }
        }

    private fun getUserIllusts(state: PictureState, userId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = userRemoteRepository.getUserIllusts(
                    UserIllustsQuery(
                        userId = userId,
                        type = Type.Illust.value
                    )
                )
            ) {
                dispatch(
                    PictureAction.UpdateUserIllustsState(userIllusts = it.illusts)
                )
            }
        }
    }
}