package com.mrl.pixiv.common.middleware.illust

import com.mrl.pixiv.common.viewmodel.Reducer

class IllustReducer : Reducer<IllustState, IllustAction> {
    override fun IllustState.reduce(action: IllustAction): IllustState {
        return when (action) {
            is IllustAction.SetIllust -> {
                illusts.put(action.illustId, action.illust)
                this
            }
        }
    }
}