package com.mrl.pixiv.picture.viewmodel

import androidx.compose.runtime.toMutableStateList
import com.mrl.pixiv.common.viewmodel.Reducer
import kotlinx.collections.immutable.toImmutableList


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

            is PictureAction.UpdateIllust -> state.copy(illust = action.illust)
            is PictureAction.UpdateUgoiraFrame -> state.copy(ugoiraImages = action.images.toImmutableList())

            else -> state
        }
    }
}