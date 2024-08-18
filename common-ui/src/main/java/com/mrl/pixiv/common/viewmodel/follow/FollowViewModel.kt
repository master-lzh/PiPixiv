package com.mrl.pixiv.common.viewmodel.follow

import com.mrl.pixiv.common.viewmodel.BaseViewModel


class FollowViewModel(
    reducer: FollowReducer,
    middleware: FollowMiddleware
) : BaseViewModel<FollowState, FollowAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = FollowState.INITIAL,
) {
}