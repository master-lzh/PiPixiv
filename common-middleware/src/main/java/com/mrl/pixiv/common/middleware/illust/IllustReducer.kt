package com.mrl.pixiv.common.middleware.illust

import com.mrl.pixiv.common.viewmodel.Reducer

class IllustReducer : Reducer<IllustState, IllustAction> {
    override fun reduce(state: IllustState, action: IllustAction): IllustState {
        return when (action) {
            is IllustAction.SetIllust -> {
                state.illusts.put(action.illustId, action.illust)
                state
            }
        }
    }
}