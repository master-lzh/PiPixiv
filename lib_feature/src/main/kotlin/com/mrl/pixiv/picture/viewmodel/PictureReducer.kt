package com.mrl.pixiv.picture.viewmodel

import androidx.compose.runtime.toMutableStateList
import com.mrl.pixiv.common.viewmodel.Reducer
import kotlinx.collections.immutable.toImmutableList
import org.koin.core.annotation.Single

@Single
class PictureReducer : Reducer<PictureState, PictureAction> {
    override fun PictureState.reduce(action: PictureAction): PictureState {
        return when (action) {
            is PictureAction.UpdateState -> action.state
            is PictureAction.UpdateUserIllustsState -> copy(userIllusts = action.userIllusts.toMutableStateList())
            is PictureAction.UpdateIllustRelatedState -> copy(
                illustRelated = action.illustRelated.toMutableStateList(),
                nextUrl = action.nextUrl
            )

            is PictureAction.UpdateIsBookmarkState -> copy(
                userIllusts = action.userIllusts.toMutableStateList(),
                illustRelated = action.illustRelated.toMutableStateList()
            )

            is PictureAction.UpdateIllust -> copy(illust = action.illust)
            is PictureAction.UpdateUgoiraFrame -> copy(ugoiraImages = action.images.toImmutableList())
            else -> this
        }
    }
}