package com.mrl.pixiv.picture.viewmodel

import com.mrl.pixiv.common.base.BaseViewModel
import com.mrl.pixiv.data.Illust

class PictureViewModel(
    illust: Illust,
    reducer: PictureReducer,
    middleware: PictureMiddleware,
) : BaseViewModel<PictureState, PictureAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = PictureState.INITIAL,
) {

    init {
        dispatch(PictureAction.GetUserIllustsIntent(illust.user.id))
        dispatch(PictureAction.GetIllustRelatedIntent(illust.id))
    }
    override fun onStart() {

    }
}