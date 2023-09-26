package com.mrl.pixiv.profile.viewmodel

import com.mrl.pixiv.common.base.BaseViewModel

class ProfileViewModel(
    reducer: ProfileReducer,
    middleware: ProfileMiddleware,
) : BaseViewModel<ProfileState, ProfileAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = ProfileState.INITIAL,
) {
    override fun onStart() {
        dispatch(ProfileAction.GetUserInfoIntent)
        dispatch(ProfileAction.GetUserBookmarksIllustIntent)
    }
}