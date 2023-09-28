package com.mrl.pixiv.picture.viewmodel

import androidx.compose.runtime.toMutableStateList
import com.mrl.pixiv.common.data.Reducer


class PictureReducer : Reducer<PictureState, PictureAction> {
    override fun reduce(state: PictureState, action: PictureAction): PictureState {
        return when (action) {
            is PictureAction.UpdateState -> action.state
            is PictureAction.UpdateUserIllustsState -> state.copy(userIllusts = action.userIllusts.toMutableStateList())
            is PictureAction.UpdateIllustRelatedState -> state.copy(
                illustRelated = action.illustRelated.toMutableStateList(),
                nextUrl = action.nextUrl
            )

            is PictureAction.UpdateIsBookmarkState -> state.copy(
                userIllusts = action.userIllusts.toMutableStateList(),
                illustRelated = action.illustRelated.toMutableStateList()
            )

            else -> state
        }
    }
}