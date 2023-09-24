package com.mrl.pixiv.picture.viewmodel

import com.mrl.pixiv.common.data.Reducer


class PictureReducer : Reducer<PictureState, PictureAction> {
    override fun reduce(state: PictureState, action: PictureAction): PictureState {
        return when (action) {
            is PictureAction.UpdateState -> action.state
            else -> state
        }
    }
}