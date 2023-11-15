package com.mrl.pixiv.common.middleware.follow

import com.mrl.pixiv.common.base.BaseViewModel


class FollowViewModel(
    reducer: FollowReducer,
    middleware: FollowMiddleware
) : BaseViewModel<FollowState, FollowAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = FollowState.INITIAL,
) {
}