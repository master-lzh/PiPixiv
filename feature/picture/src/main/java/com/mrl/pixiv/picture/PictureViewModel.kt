package com.mrl.pixiv.picture

import androidx.compose.runtime.toMutableStateList
import com.mrl.pixiv.common.base.BaseScreenViewModel
import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.data.Type
import com.mrl.pixiv.data.illust.IllustRelatedQuery
import com.mrl.pixiv.data.user.UserIllustsQuery
import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.repository.remote.IllustRemoteRepository
import com.mrl.pixiv.repository.remote.UserRemoteRepository

class PictureViewModel(
    private val userLocalRepository: UserLocalRepository,
    private val userRemoteRepository: UserRemoteRepository,
    private val illustRemoteRepository: IllustRemoteRepository,
) : BaseScreenViewModel<PictureUiState, PictureUiIntent>() {

    var nextUrl: String = ""

    override fun handleUserIntent(intent: PictureUiIntent) {
        when (intent) {
            is PictureUiIntent.GetUserIllustsIntent -> getUserIllusts(intent.userId)
            is PictureUiIntent.GetIllustRelatedIntent -> getIllustRelated(intent.illustId)
            is PictureUiIntent.LoadMoreIllustRelatedIntent -> loadMoreIllustRelated(intent.queryMap)
        }
    }

    private fun loadMoreIllustRelated(queryMap: Map<String, String>?) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.loadMoreIllustRelated(
                    queryMap ?: return@launchNetwork
                )
            ) {
                if (it != null) {
                    updateUiState { apply { illustRelated.addAll(it.illusts) } }
                    nextUrl = it.nextURL
                }
            }
        }
    }

    private fun getIllustRelated(illustId: Long) {
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
                    updateUiState { apply { illustRelated = it.illusts.toMutableStateList() } }
                    nextUrl = it.nextURL
                }
            }
        }
    }

    private fun getUserIllusts(userId: Long) {
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
                    updateUiState { apply { userIllusts = it.illusts.toMutableStateList() } }
                }
            }
        }
    }

    override fun initUiState(): PictureUiState = PictureUiState()
}