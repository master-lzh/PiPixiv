package com.mrl.pixiv.common.viewmodel.illust

import androidx.collection.LruCache
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.Illust

data class IllustState(
    val illusts: LruCache<Long, Illust>,
) : State {
    companion object {
        val INITIAL = IllustState(
            illusts = LruCache(100)
        )
    }
}

sealed class IllustAction : Action {
    data class SetIllust(val illustId: Long, val illust: Illust) : IllustAction()
}


class IllustViewModel(
    reducer: IllustReducer,
    authMiddleware: IllustMiddleware,
) : BaseViewModel<IllustState, IllustAction>(
    initialState = IllustState.INITIAL,
    reducer = reducer,
    middlewares = listOf(authMiddleware)
)