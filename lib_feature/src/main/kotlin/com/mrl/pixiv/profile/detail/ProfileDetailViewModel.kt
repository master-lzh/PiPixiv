package com.mrl.pixiv.profile.detail

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.data.Novel
import com.mrl.pixiv.common.data.Restrict
import com.mrl.pixiv.common.data.Type
import com.mrl.pixiv.common.data.user.UserDetailResp
import com.mrl.pixiv.common.datasource.local.mmkv.requireUserInfoValue
import com.mrl.pixiv.common.repository.PixivRepository
import com.mrl.pixiv.common.viewmodel.BaseMviViewModel
import com.mrl.pixiv.common.viewmodel.ViewIntent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import org.koin.android.annotation.KoinViewModel

@Stable
data class ProfileDetailState(
    val userTotalWorks: Int = 0,
    val userIllusts: ImmutableList<Illust> = persistentListOf(),
    val userBookmarksIllusts: ImmutableList<Illust> = persistentListOf(),
    val userBookmarksNovels: ImmutableList<Novel> = persistentListOf(),
    val userInfo: UserDetailResp = UserDetailResp(),
)

sealed class ProfileDetailAction : ViewIntent {
    data object LoadUserData : ProfileDetailAction()
}

@KoinViewModel
class ProfileDetailViewModel(
    private val uid: Long?,
) : BaseMviViewModel<ProfileDetailState, ProfileDetailAction>(
    initialState = ProfileDetailState(),
) {
    init {
        dispatch(ProfileDetailAction.LoadUserData)
    }

    override suspend fun handleIntent(intent: ProfileDetailAction) {
        when (intent) {
            is ProfileDetailAction.LoadUserData -> loadUserData()
        }
    }

    private fun loadUserData() {
        launchIO {
            val userId = uid ?: requireUserInfoValue.user.id
            async {
                val resp = PixivRepository.getUserIllusts(
                    userId = userId,
                    type = Type.Illust.value,
                )
                updateState {
                    copy(
                        userIllusts = resp.illusts.toImmutableList(),
                    )
                }
            }
            async {
                val resp = PixivRepository.getUserBookmarksNovels(
                    restrict = Restrict.PUBLIC,
                    userId = userId
                )
                updateState {
                    copy(
                        userBookmarksNovels = resp.novels.toImmutableList(),
                    )
                }
            }
            async {
                val resp = PixivRepository.getUserBookmarksIllust(
                    restrict = Restrict.PUBLIC,
                    userId = userId
                )
                updateState {
                    copy(
                        userBookmarksIllusts = resp.illusts.toImmutableList(),
                    )
                }
            }
            async {
                val resp = PixivRepository.getUserDetail(userId = userId)
                updateState {
                    copy(
                        userInfo = resp
                    )
                }
            }
        }
    }
}