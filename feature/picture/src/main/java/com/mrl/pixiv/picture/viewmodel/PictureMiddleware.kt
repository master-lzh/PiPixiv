package com.mrl.pixiv.picture.viewmodel

import com.mrl.pixiv.common.data.Middleware
import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.data.Type
import com.mrl.pixiv.data.illust.IllustRelatedQuery
import com.mrl.pixiv.data.user.UserIllustsQuery
import com.mrl.pixiv.repository.remote.IllustRemoteRepository
import com.mrl.pixiv.repository.remote.UserRemoteRepository


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

            else -> {}
        }
    }

    private fun loadMoreIllustRelated(state: PictureState, queryMap: Map<String, String>?) =
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.loadMoreIllustRelated(
                    queryMap ?: return@launchNetwork
                )
            ) {
                if (it != null) {
                    dispatch(
                        PictureAction.UpdateState(
                            state.copy(
                                illustRelated = state.illustRelated + it.illusts,
                                nextUrl = it.nextURL
                            )
                        )
                    )
                }
            }
        }

    private fun getIllustRelated(state: PictureState, illustId: Long) =
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.getIllustRelated(
                    IllustRelatedQuery(
                        illustId = illustId,
                        filter = Filter.ANDROID.filter
                    )
                )
            ) {
                if (it != null) {
                    dispatch(
                        PictureAction.UpdateState(
                            state.copy(
                                illustRelated = it.illusts,
                                nextUrl = it.nextURL
                            )
                        )
                    )
                }
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
                if (it != null) {
                    dispatch(
                        PictureAction.UpdateState(
                            state.copy(
                                userIllusts = it.illusts,
                            )
                        )
                    )
                }
            }
        }
    }
}