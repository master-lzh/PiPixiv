package com.mrl.pixiv.profile.detail.viewmodel

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.data.Novel
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.profile.detail.state.UserInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.android.annotation.KoinViewModel

@Stable
data class ProfileDetailState(
    val userTotalWorks: Int,
    val userIllusts: ImmutableList<Illust>,
    val userBookmarksIllusts: ImmutableList<Illust>,
    val userBookmarksNovels: ImmutableList<Novel>,
    val userInfo: UserInfo,
) : State {
    companion object {
        val INITIAL = ProfileDetailState(
            userTotalWorks = 0,
            userIllusts = persistentListOf(),
            userBookmarksIllusts = persistentListOf(),
            userBookmarksNovels = persistentListOf(),
            userInfo = UserInfo(),
        )
    }
}

sealed class ProfileDetailAction : Action {
    data class GetUserInfoIntent(val uid: Long) : ProfileDetailAction()
    data class GetUserIllustsIntent(val uid: Long) : ProfileDetailAction()
    data class GetUserBookmarksIllustIntent(val uid: Long) : ProfileDetailAction()
    data class GetUserBookmarksNovelIntent(val uid: Long) : ProfileDetailAction()

    data class UpdateUserInfo(val userInfo: UserInfo) : ProfileDetailAction()
    data class UpdateUserBookmarksIllusts(val illusts: List<Illust>) : ProfileDetailAction()

    data class UpdateUserBookmarksNovels(val novels: List<Novel>) : ProfileDetailAction()
    data class UpdateUserIllusts(val illusts: List<Illust>) : ProfileDetailAction()
}

@KoinViewModel
class ProfileDetailViewModel(
    uid: Long,
    reducer: ProfileDetailReducer,
    middleware: ProfileDetailMiddleware,
) : BaseViewModel<ProfileDetailState, ProfileDetailAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = ProfileDetailState.INITIAL,
) {
    init {
        dispatch(ProfileDetailAction.GetUserInfoIntent(uid))
        dispatch(ProfileDetailAction.GetUserIllustsIntent(uid))
        dispatch(ProfileDetailAction.GetUserBookmarksIllustIntent(uid))
        dispatch(ProfileDetailAction.GetUserBookmarksNovelIntent(uid))
    }
}