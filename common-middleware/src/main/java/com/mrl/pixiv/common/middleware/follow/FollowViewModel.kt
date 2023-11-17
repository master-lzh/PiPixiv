package com.mrl.pixiv.common.middleware.follow

import com.mrl.pixiv.common.base.BaseViewModel
import com.mrl.pixiv.data.Illust


class FollowViewModel(
    illust: Illust,
    reducer: FollowReducer,
    middleware: FollowMiddleware
) : BaseViewModel<FollowState, FollowAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = FollowState.INITIAL,
) {
    init {
        dispatch(
            FollowAction.UpdateFollowState(
                userId = illust.user.id,
                isFollowed = illust.user.isFollowed
            )
        )
    }
}