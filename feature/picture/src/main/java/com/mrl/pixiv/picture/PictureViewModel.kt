package com.mrl.pixiv.picture

import androidx.compose.runtime.toMutableStateList
import com.mrl.pixiv.common.base.BaseScreenViewModel
import com.mrl.pixiv.data.Type
import com.mrl.pixiv.data.user.UserIllustsQuery
import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.repository.remote.UserRemoteRepository

class PictureViewModel(
    private val userLocalRepository: UserLocalRepository,
    private val userRemoteRepository: UserRemoteRepository,
) : BaseScreenViewModel<PictureUiState, PictureUiIntent>() {

    override fun handleUserIntent(intent: PictureUiIntent) {
        when (intent) {
            is PictureUiIntent.GetUserIllustsIntent -> getUserIllusts(intent.userId)
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