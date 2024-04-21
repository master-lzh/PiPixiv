package com.mrl.pixiv.profile.detail.viewmodel

import com.mrl.pixiv.common.viewmodel.BaseViewModel

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