package com.mrl.pixiv.profile.detail.viewmodel

import com.mrl.pixiv.common.viewmodel.BaseViewModel

class ProfileDetailViewModel(
    reducer: ProfileDetailReducer,
    middleware: ProfileDetailMiddleware,
) : BaseViewModel<ProfileDetailState, ProfileDetailAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = ProfileDetailState.INITIAL,
) {
    override fun onStart() {
        dispatch(ProfileDetailAction.GetUserInfoIntent)
        dispatch(ProfileDetailAction.GetUserBookmarksIllustIntent)
        dispatch(ProfileDetailAction.GetUserBookmarksNovelIntent)
    }
}